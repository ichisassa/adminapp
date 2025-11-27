const API_ENDPOINT = '/api/admin/mail/logs';
const DEFAULT_PAGE = 1;
const DEFAULT_PAGE_SIZE = 20;
const MAX_PAGINATION_DISPLAY = 5;

interface MailLogSummary {
  id: number;
  sentAt: string | null;
  status: string;
  toAddress: string;
  subject: string;
  isHtml: boolean;
  errorMessage: string | null;
}

interface MailLogSearchResponse {
  items: MailLogSummary[];
  totalSize: number;
  page: number;
  size: number;
  totalPages: number;
  hasNext: boolean;
  hasPrevious: boolean;
}

interface MailLogSearchCondition {
  sentAtFrom: string;
  sentAtTo: string;
  status: string;
  toAddress: string;
  subject: string;
}

class MailLogListPage {
  private currentPage = DEFAULT_PAGE;
  private readonly pageSize = DEFAULT_PAGE_SIZE;
  private lastCondition: MailLogSearchCondition | null = null;

  private tableBody: HTMLTableSectionElement | null = null;
  private paginationContainer: HTMLElement | null = null;
  private summaryElement: HTMLElement | null = null;

  /**
   * 画面ロード時に必要な初期化をまとめて行う。
   * DOM 要素のキャッシュ、イベント登録、初回検索を実行する。
   */
  init(): void {
    this.cacheElements();
    this.registerEventHandlers();
    this.executeSearch(DEFAULT_PAGE);
  }

  /**
   * 描画に利用する主要な DOM 要素を取得し、プロパティへ保持する。
   */
  private cacheElements(): void {
    this.tableBody = document.getElementById('mailLogTableBody') as HTMLTableSectionElement | null;
    this.paginationContainer = document.getElementById('mailLogPagination');
    this.summaryElement = document.getElementById('mailLogSummary');
  }

  /**
   * 検索ボタン・クリアボタン・ページネーションのイベントを登録する。
   */
  private registerEventHandlers(): void {
    const searchButton = document.getElementById('btnSearch');
    const clearButton = document.getElementById('btnClear');

    if (searchButton) {
      searchButton.addEventListener('click', () => this.handleSearchButton());
    }
    if (clearButton) {
      clearButton.addEventListener('click', () => this.handleClearButton());
    }
    if (this.paginationContainer) {
      this.paginationContainer.addEventListener('click', (event) => this.handlePaginationClick(event));
    }
  }

  /**
   * 検索ボタンクリック時の処理。
   * 現在のフォーム値を条件として保存し、1ページ目で検索する。
   */
  private handleSearchButton(): void {
    this.lastCondition = this.collectSearchCondition();
    this.executeSearch(DEFAULT_PAGE);
  }

  /**
   * 条件クリアボタンクリック時の処理。
   * フォームをリセットし、初期条件で検索する。
   */
  private handleClearButton(): void {
    this.resetForm();
    this.lastCondition = this.collectSearchCondition();
    this.executeSearch(DEFAULT_PAGE);
  }

  /**
   * ページネーションのリンク押下時にページ番号を取得して再検索する。
   */
  private handlePaginationClick(event: Event): void {
    const target = event.target as HTMLElement;
    if (!target) {
      return;
    }
    const anchor = target.closest<HTMLAnchorElement>('a[data-page]');
    if (!anchor) {
      return;
    }
    event.preventDefault();
    const page = Number(anchor.dataset.page);
    if (Number.isNaN(page) || page < 1 || page === this.currentPage) {
      return;
    }
    this.executeSearch(page);
  }

  /**
   * 各フォームから入力値を取得して検索条件オブジェクトを生成する。
   */
  private collectSearchCondition(): MailLogSearchCondition {
    return {
      sentAtFrom: this.getInputValue('sentAtFrom'),
      sentAtTo: this.getInputValue('sentAtTo'),
      status: this.getSelectValue('status'),
      toAddress: this.getInputValue('toAddress'),
      subject: this.getInputValue('subject'),
    };
  }

  /**
   * 指定 ID のテキスト入力値をトリムして取得する。
   */
  private getInputValue(id: string): string {
    const element = document.getElementById(id) as HTMLInputElement | null;
    return element ? element.value.trim() : '';
  }

  /**
   * 指定 ID のセレクトボックス値を取得する。
   */
  private getSelectValue(id: string): string {
    const element = document.getElementById(id) as HTMLSelectElement | null;
    return element ? element.value : '';
  }

  /**
   * 検索フォームを初期状態に戻す。
   */
  private resetForm(): void {
    const inputIds = ['sentAtFrom', 'sentAtTo', 'toAddress', 'subject'];
    inputIds.forEach((id) => {
      const element = document.getElementById(id) as HTMLInputElement | null;
      if (element) {
        element.value = '';
      }
    });
    const statusSelect = document.getElementById('status') as HTMLSelectElement | null;
    if (statusSelect) {
      statusSelect.value = '';
    }
  }

  /**
   * 現在の条件と指定ページで検索 API を呼び出し、結果を画面に反映する。
   */
  private async executeSearch(page: number): Promise<void> {
    try {
      const params = this.buildQueryParams(page);
      const response = await fetch(`${API_ENDPOINT}?${params.toString()}`, {
        headers: {
          'Accept': 'application/json',
        },
      });
      if (!response.ok) {
        throw new Error(`HTTP error ${response.status}`);
      }
      const data = (await response.json()) as MailLogSearchResponse;
      this.currentPage = page;
      this.renderTable(data.items);
      this.renderPagination(data);
      this.renderSummary(data);
    } catch (error) {
      console.error('Failed to fetch mail logs', error);
      window.alert('検索中にエラーが発生しました。時間をおいて再度お試しください。');
    }
  }

  /**
   * フォーム条件とページ情報を組み合わせ、クエリ文字列を生成する。
   */
  private buildQueryParams(page: number): URLSearchParams {
    const params = new URLSearchParams();
    const condition = this.lastCondition ?? this.collectSearchCondition();

    const append = (key: keyof MailLogSearchCondition, value: string) => {
      if (value) {
        params.append(key, value);
      }
    };

    append('sentAtFrom', condition.sentAtFrom);
    append('sentAtTo', condition.sentAtTo);
    append('status', condition.status);
    append('toAddress', condition.toAddress);
    append('subject', condition.subject);

    params.append('page', String(page));
    params.append('size', String(this.pageSize));

    return params;
  }

  /**
   * 検索結果の配列をもとに一覧テーブルを描画する。
   * データが空の場合は空表示行を挿入する。
   */
  private renderTable(items: MailLogSummary[]): void {
    if (!this.tableBody) {
      return;
    }
    this.tableBody.innerHTML = '';

    if (!items || items.length === 0) {
      const row = document.createElement('tr');
      const cell = document.createElement('td');
      cell.colSpan = 8;
      cell.className = 'text-center text-muted';
      cell.textContent = '該当するメール送信ログはありません。';
      row.appendChild(cell);
      this.tableBody.appendChild(row);
      return;
    }

    items.forEach((item) => {
      const row = document.createElement('tr');
      row.appendChild(this.createCheckboxCell());
      row.appendChild(this.createTextCell(this.formatDateTime(item.sentAt)));
      row.appendChild(this.createStatusCell(item.status));
      row.appendChild(this.createTextCell(item.toAddress));
      row.appendChild(this.createTextCell(item.subject));
      row.appendChild(this.createTextCell(item.isHtml ? 'HTML' : 'Text'));
      row.appendChild(this.createTextCell(this.truncate(item.errorMessage)));
      row.appendChild(this.createActionCell(item.id));
      this.tableBody!.appendChild(row);
    });
  }

  /**
   * 行頭のチェックボックスセルを作成する。
   */
  private createCheckboxCell(): HTMLTableCellElement {
    const cell = document.createElement('td');
    cell.className = 'text-center';
    const checkbox = document.createElement('input');
    checkbox.type = 'checkbox';
    cell.appendChild(checkbox);
    return cell;
  }

  /**
   * 指定テキストを表示するセルを作成する。
   */
  private createTextCell(text: string): HTMLTableCellElement {
    const cell = document.createElement('td');
    cell.textContent = text;
    return cell;
  }

  /**
   * ステータス表示のバッジを含むセルを作成する。
   */
  private createStatusCell(status: string): HTMLTableCellElement {
    const cell = document.createElement('td');
    const span = document.createElement('span');
    span.className = `badge ${this.resolveStatusBadgeClass(status)}`;
    span.textContent = status || '-';
    cell.appendChild(span);
    return cell;
  }

  /**
   * 詳細・再送ボタンをまとめた操作セルを作成する。
   */
  private createActionCell(id: number): HTMLTableCellElement {
    const cell = document.createElement('td');
    cell.className = 'text-nowrap';

    const detailButton = document.createElement('button');
    detailButton.className = 'btn btn-outline-primary btn-sm mr-1';
    detailButton.textContent = '詳細';
    detailButton.dataset.id = String(id);
    detailButton.addEventListener('click', () => window.alert('詳細機能は準備中です'));

    const resendButton = document.createElement('button');
    resendButton.className = 'btn btn-outline-secondary btn-sm';
    resendButton.textContent = '再送';
    resendButton.dataset.id = String(id);
    resendButton.addEventListener('click', () => window.alert('再送機能は準備中です'));

    cell.appendChild(detailButton);
    cell.appendChild(resendButton);
    return cell;
  }

  /**
   * 総件数と現在ページからページネーションのリンク群を描画する。
   */
  private renderPagination(data: MailLogSearchResponse): void {
    if (!this.paginationContainer) {
      return;
    }
    this.paginationContainer.innerHTML = '';

    const totalPages = data.totalPages > 0
      ? data.totalPages
      : Math.max(1, Math.ceil(data.totalSize / data.size));
    const currentPage = data.page + 1;

    const appendPageItem = (label: string, page: number, disabled = false, active = false) => {
      const li = document.createElement('li');
      li.className = `page-item${disabled ? ' disabled' : ''}${active ? ' active' : ''}`;
      const anchor = document.createElement('a');
      anchor.className = 'page-link';
      anchor.href = '#';
      if (!disabled) {
        anchor.dataset.page = String(page);
      }
      anchor.textContent = label;
      li.appendChild(anchor);
      this.paginationContainer!.appendChild(li);
    };

    appendPageItem('«', currentPage - 1, currentPage <= 1);

    const half = Math.floor(MAX_PAGINATION_DISPLAY / 2);
    let start = Math.max(1, currentPage - half);
    let end = start + MAX_PAGINATION_DISPLAY - 1;
    if (end > totalPages) {
      end = totalPages;
      start = Math.max(1, end - MAX_PAGINATION_DISPLAY + 1);
    }

    for (let i = start; i <= end; i += 1) {
      appendPageItem(String(i), i, false, i === currentPage);
    }

    appendPageItem('»', currentPage + 1, currentPage >= totalPages);
  }

  /**
   * 件数サマリのテキストを更新する。
   */
  private renderSummary(data: MailLogSearchResponse): void {
    if (!this.summaryElement) {
      return;
    }
    const itemCount = Array.isArray(data.items) ? data.items.length : 0;
    const totalCount = typeof data.totalSize === 'number' ? data.totalSize : 0;
    const size = typeof data.size === 'number' && data.size > 0 ? data.size : DEFAULT_PAGE_SIZE;
    const pageIndex = typeof data.page === 'number' && data.page >= 0 ? data.page : 0;

    if (totalCount === 0 || itemCount === 0) {
      this.summaryElement.textContent = '該当するデータがありません。';
      return;
    }
    const start = (pageIndex * size) + 1;
    const end = Math.min(start + itemCount - 1, totalCount);
    this.summaryElement.textContent = `全 ${totalCount} 件中 ${start}〜${end} 件を表示`;
  }

  /**
   * ステータス値に応じた Bootstrap バッジのクラス名を返す。
   */
  private resolveStatusBadgeClass(status: string): string {
    const normalized = (status || '').toUpperCase();
    switch (normalized) {
      case 'SUCCESS':
        return 'badge-success';
      case 'FAILED':
      case 'ERROR':
        return 'badge-danger';
      case 'PENDING':
      case 'QUEUED':
        return 'badge-warning';
      default:
        return 'badge-secondary';
    }
  }

  /**
   * ISO 文字列などで渡された日時を「YYYY/MM/DD HH:mm」形式に整形する。
   */
  private formatDateTime(value: string | null): string {
    if (!value) {
      return '-';
    }
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      return value;
    }
    const year = date.getFullYear();
    const month = this.pad(date.getMonth() + 1);
    const day = this.pad(date.getDate());
    const hours = this.pad(date.getHours());
    const minutes = this.pad(date.getMinutes());
    return `${year}/${month}/${day} ${hours}:${minutes}`;
  }

  /**
   * 数値を2桁にゼロ埋めする。
   */
  private pad(value: number): string {
    return value < 10 ? `0${value}` : `${value}`;
  }

  /**
   * エラーメッセージなどの長文を指定長で丸め、末尾に省略記号を付与する。
   */
  private truncate(value: string | null, length = 40): string {
    if (!value) {
      return '-';
    }
    if (value.length <= length) {
      return value;
    }
    return `${value.substring(0, length)}...`;
  }
}

/** DOM 準備完了後に一覧ページを初期化する。 */
document.addEventListener('DOMContentLoaded', () => {
  const page = new MailLogListPage();
  page.init();
});
