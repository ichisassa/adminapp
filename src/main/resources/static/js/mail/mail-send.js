(function () {
  'use strict';

  function MailSendPage() {
    this.sendButton = null;
  }

  MailSendPage.prototype.init = function init() {
    this.cacheElements();
    this.registerEvents();
  };

  MailSendPage.prototype.cacheElements = function cacheElements() {
    this.sendButton = document.getElementById('btnSendMail');
  };

  MailSendPage.prototype.registerEvents = function registerEvents() {
    if (!this.sendButton) {
      return;
    }
    this.sendButton.addEventListener('click', this.handleSendClick.bind(this));
  };

  MailSendPage.prototype.handleSendClick = function handleSendClick(event) {
    event.preventDefault();
    // TODO: 第二段階で AJAX 経由の送信処理を実装する
  };

  document.addEventListener('DOMContentLoaded', function onReady() {
    var page = new MailSendPage();
    page.init();
  });
}());
