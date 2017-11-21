$('.share-button').click(
  function (event) {
    event.preventDefault();
    var $this = $(this);
    window.open($this.attr('href'), $this.attr('title'), 'width=640,height=300');
  }
);
