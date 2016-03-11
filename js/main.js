(function() {
  $(document).ready(function() {

    var fontSize = 1;
    var editors = [];

    $('.code').each(function (index, editorElement) {
      var editor = ace.edit(editorElement);

      var srcFile = $(editorElement).attr('data-src-file');
      var fileType = $(editorElement).attr('data-file-type');
      var fileExtension = fileType;

      if (!fileType) {
        var fileNameComponents = srcFile.split('.');
        fileExtension = fileNameComponents[fileNameComponents.length - 1];
      }

      editor.$blockScrolling = Infinity;
      editor.setTheme('ace/theme/monokai');
      editor.getSession().setMode('ace/mode/' + fileExtension);
      editor.setReadOnly(true);
      editor.setValue('Loading...', -1);

      editorElement.style.fontSize = fontSize + 'em';
      editorElement.style.height = '1em';
      editor.resize();

      editors.push({
        element: editorElement,
        editor: editor,
        srcFile: srcFile,
        fileExtension: fileExtension
      });
    });

    editors.forEach(function (e) {

      var editorElement = e.element;
      var editor = e.editor;
      var srcFile = e.srcFile;
      var fileExtension = e.fileExtension;

      $.ajax(srcFile, {
        dataType: 'text',

        success: function(data) {
          var doc = editor.getSession().getDocument();

          editor.setValue(data, -1);

          editorElement.style.height = (fontSize + 0.1) * doc.getLength() + 'em';
          editor.resize();
        },

        error: function() {
          editor.setValue('contents not available', -1);
        }
      });
    });

  });
}());
