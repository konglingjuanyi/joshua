/**
 * 文章编辑器组件
 * Created by y27chen on 2016/10/29.
 */
import "../../css/lib-quill.snow.css";
import "../../node_modules/lightgallery/dist/css/lightgallery.css";
import Quill from "../lib/quill";
import "../lib/quill-core";
import "lightgallery";

const EDITOR_CONFIG = {
    PLACEHOLDER: "在这里编写您的文章",
    TOOLBAR: [
        /* 基本控件 */
        [{'header': [1, 2, 3, 4, 5, 6, false]}],

        /* 文字样式控件 */
        ['bold', 'italic', 'underline', 'strike'],
        [{'color': []}, {'background': []}],

        /* 文字对齐控件 */
        [{'align': []}],
        [{'list': 'ordered'}, {'list': 'bullet'}],
        [{'indent': '-1'}, {'indent': '+1'}],

        /* 其他控件 */
        ['link', 'image'],
        [{'script': 'sub'}, {'script': 'super'}],
        ['clean']
    ],
    THEME: "snow"
};

export default class Editor {
    constructor() {
        this.editor = null;
    }

    loadAsEditor() {
        this.editor = new Quill("#article-editor .container", {
            placeholder: EDITOR_CONFIG.PLACEHOLDER,
            modules: {
                toolbar: EDITOR_CONFIG.TOOLBAR
            },
            theme: EDITOR_CONFIG.THEME
        });
    }

    loadAsReader() {
        this.editor = new Quill("#article-reader .container", {
            modules: {
                toolbar: false
            },
            theme: EDITOR_CONFIG.THEME,
            readOnly: true
        });
    }

    loadContent(id, onSuccess) {
        let editor = this.editor;
        let dimmer = $(".ui.dimmer");
        dimmer.dimmer("show");
        if (id && id > 0) {
            $.ajax({
                url: "/article/content/" + id,
                type: "get",
                processData: false,
                contentType: false,
                success: (content) => {
                    if (content) {
                        var articleContent = JSON.parse(content);
                        editor.setContents(articleContent);

                        if (onSuccess) {
                            onSuccess.apply(editor);
                        }

                        dimmer.dimmer("hide");
                    }
                },
                error: () => {
                    dimmer.dimmer("hide");
                }
            });
        } else {
            dimmer.dimmer("hide");
        }
    }

    initContent() {
        var reader = $("#article-reader");

        reader.find("img").each(function () {
            var imgSrc = $(this).attr("src");
            $(this).attr("data-src", imgSrc);
        });

        reader.lightGallery({
            selector: 'img'
        });

        reader.removeAttr("style");
        $(".ui.dimmer").removeClass("active");
    }
}
