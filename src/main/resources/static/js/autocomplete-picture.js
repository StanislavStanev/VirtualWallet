$(function () {

    $("#recipient").autocomplete({
        source: //"autocomplete.php",
            [
                {
                    id: "John Doe",
                    value: "John Doe",
                    label: "John Doe",
                    img: "http://images.maskworld.com/is/image/maskworld/bigview/john-doe-foam-latex-mask--mw-117345-1.jpg"
                },
                {
                    id: "system Admin",
                    value: "system Admin",
                    label: "system Admin",
                    img: "http://www.ericom.com/imgs/braftonArticles/3-things-every-system-admin-should-know-about-virtualization_16001411_800906167_0_14057272_500.jpg"
                }
            ],
        minLength: 1,
        select: function (event, ui) {
            /*
            var url = ui.item.id;
            if(url != '') {
              location.href = '...' + url;
            }
            */
        },
        html: true,
        open: function (event, ui) {
            $(".ui-autocomplete").css("z-index", 1000);

        }
    })
        .autocomplete("instance")._renderItem = function (ul, item) {
        return $("<li><div><img src='" + item.img + "'><span>" + item.value + "</span></div></li>").appendTo(ul);
    };

});