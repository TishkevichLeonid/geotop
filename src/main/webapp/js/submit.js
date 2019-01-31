//CONST
var LAT_DEFAULT_SPB = 59.934932;
var LONG_DEFAULT_SPB = 30.333555;
var ZOOM_DEFAULT_SPB = 12;

var map;

ymaps.ready(function () {
    map = new ymaps.Map("map", {
        center: [LAT_DEFAULT_SPB, LONG_DEFAULT_SPB],
        zoom: ZOOM_DEFAULT_SPB
    });
    map.controls.remove('searchControl');
    map.controls.remove('trafficControl');
    map.controls.remove('typeSelector');
   // map.controls.remove('fullscreenControl');
    map.controls.remove('rulerControl');
    // document.getElementsByClassName("ymaps-2-1-56-map ymaps-2-1-56-i-ua_js_yes ymaps-2-1-56-map-bg-ru ymaps-2-1-56-islets_map-lang-ru")[0].style = "width: 35em; height:30em;"

    setTop();
});

function setTop() {

    var top_div1 = document.getElementById('top1');
    var top_img1 = document.getElementById('img1');
    var top_title1 = document.getElementById('title1');
    var top_rate1 = document.getElementById('rating1');
    var top_id1 = document.getElementById('topId1');

    var top_div2 = document.getElementById('top2');
    var top_img2 = document.getElementById('img2');
    var top_title2 = document.getElementById('title2');
    var top_rate2 = document.getElementById('rating2');
    var top_id2 = document.getElementById('topId2');

    var top_div3 = document.getElementById('top3');
    var top_img3 = document.getElementById('img3');
    var top_title3 = document.getElementById('title3');
    var top_rate3 = document.getElementById('rating3');
    var top_id3 = document.getElementById('topId3');


    $.ajax({
        url: '/rest/top_places',
        type: 'GET',
        data: 'type=' + "Cafe",
        success: function (data) {
            var topPlaces = JSON.parse(JSON.stringify(data));

            top_div1.addEventListener("click", function(){
                showPlaceFromTop(topPlaces[0]);
            });
            top_img1.src = topPlaces[0].picture;
            top_title1.innerHTML = topPlaces[0].title;
            top_rate1.innerHTML = topPlaces[0].overallRating + "<br/>" +
                                                topPlaces[0].overallPopularity;
            top_id1.innerHTML = topPlaces[0].id;

            top_div2.addEventListener("click", function(){
                showPlaceFromTop(topPlaces[1]);
            });
            top_img2.src = topPlaces[1].picture;
            top_title2.innerHTML = topPlaces[1].title;
            top_rate2.innerHTML = topPlaces[1].overallRating + "<br/>" +
                topPlaces[1].overallPopularity;
            top_id2.innerHTML = topPlaces[1].id;

            top_div3.addEventListener("click", function(){
                showPlaceFromTop(topPlaces[2]);
            });
            top_img3.src = topPlaces[2].picture;
            top_title3.innerHTML = topPlaces[2].title;
            top_rate3.innerHTML = topPlaces[2].overallRating + "<br/>" +
                topPlaces[2].overallPopularity;
            top_id3.innerHTML = topPlaces[2].id;
        },
        error: function () {
            document.getElementById("sectionTop").style.display = "none"
        }

    });
}

function showPlaceFromTop(place) {
    showLoading();
    map.geoObjects.removeAll();

    var placeMark = new ymaps.Placemark([place.latitude, place.longitude], {
        // hintContent: "Latest checkins: " + place.vkCheckins.length,
        balloonContent: place.title
        }, {
        iconColor: '#ff0000'
    });
    placeMark.events.add('click', function () {
        showSinglePlace(place.id);
    });
    map.geoObjects.add(placeMark);

    showSinglePlace(place.id);
    hideLoading();

}

function getMarks() {
    document.getElementById("map").hidden = false;
    map.geoObjects.removeAll();
    map.setCenter([LAT_DEFAULT_SPB, LONG_DEFAULT_SPB], ZOOM_DEFAULT_SPB);

    var type = document.getElementById("type").value;
    var time = document.getElementById("time").value;
    var placeMarksList = [];
    $.ajax({
        url: '/searchPlaces',
        type: 'POST',
        data: 'type=' + type + '&time=' + time,
        success: function (data) {
            var str= '<div class = "4u"><div class="table-wrapper"><table id="ratingTable"><tbody>';
            if (data.valueOf() === '[]'){
                str = str.concat('<tr><td>' + '' + '</td><td>' + 'Sorry, no checkins for type: ' + type + '</td></tr>');
                str.concat('</tbody></table></div></div>');
                $('#rating').html(str);
            } else {
                var markList = jQuery.parseJSON(data);
                var enumeration = 1;
                jQuery.each(markList, function (key, value) {
                    var placeMark = new ymaps.Placemark([value.latitude, value.longitude], {
                        hintContent: "Latest checkins: " + value.checkinsAmount,
                        balloonContent: value.title
                    });
                    placeMarksList[enumeration] = placeMark;
                    map.geoObjects.add(placeMark);
                    str = str.concat('<tr><td>' + enumeration + '</td><td>' + value.title + '</td></tr>');
                    ++enumeration;
                });
                str.concat('</tbody></table></div></div>');
                $('#rating').html(str);

                var table = document.getElementById('ratingTable');
                var rows = table.getElementsByTagName('tr');
                for (var i = 0; i < rows.length; i++){
                    var currentRow = table.rows[i];
                    var createClickHandler =
                        function(row)
                        {
                            return function() {
                                var cell = row.getElementsByTagName("td")[0];
                                var id = cell.innerHTML;
                                var newMark = placeMarksList[id];
                                newMark.balloon.open();
                                map.setCenter(newMark.geometry.getCoordinates());
                            };
                        };
                    currentRow.onclick = createClickHandler(currentRow);
                }
            }
        }
    })
}

function getPlaces() {
    map.geoObjects.removeAll();
    map.setCenter([LAT_DEFAULT_SPB, LONG_DEFAULT_SPB], ZOOM_DEFAULT_SPB);

    var type = document.getElementById("type").value;
    var placeMarksList = [];
    var id = "id";
    var placeList = "placeList";
    showLoading();
    $.ajax({
        url: '/rest/places',
        type: 'GET',
        data: 'type=' + type,
        success: function (data) {
            hideLoading();
            var markList = JSON.parse(JSON.stringify(data));
            var enumeration = 1;
            jQuery.each(markList, function (key, value) {
                var placeMark = new ymaps.Placemark([value.latitude, value.longitude], {
                    // hintContent: "Latest checkins: " + value.vkCheckins.length,
                    balloonContent: value.title
                });
                map.geoObjects.add(placeMark);
                placeMark.events.add('click', function () {
                    showSinglePlace(value.id);
                });

                var place = {};
                place[id]=value.id;
                place[placeList]=placeMark;
                placeMarksList[enumeration] = place;

                ++enumeration;
            });

        }
    })
}

function showSinglePlace(id){
    // alert(id);
    hideSelectors();
    getPlace(id);
}

function hideSelectors() {
    var selectors = $(".selectors");
    var single = $("#single");

    if(selectors.is(':visible')) {
        selectors.hide();
        single.show("slow");
    }
}

function hideSinglePlace() {
    var selectors = $(".selectors");
    var single = $("#single");

    single.hide();
    selectors.show("slow");
}

function getPlace(id) {
    showLoading();
    $.ajax({
        url: '/rest/place',
        type: 'GET',
        data: 'id=' + id,
        success: function (data) {
            hideLoading();
            var place = JSON.parse(JSON.stringify(data));
            var img_container = document.getElementById('avatar');
            var pl_title = document.getElementById('pl_title');
            var pl_addr = document.getElementById('pl_address');
            // var pl_coords = document.getElementById('pl_coords');
            var div_vk_rate = document.getElementById('div_vk_rate');
            var div_fb_rate = document.getElementById('div_fb_rate');
            var pl_vk_rating = document.getElementById('pl_vk_rating');
            var pl_fb_rating = document.getElementById('pl_fb_rating');
            var pl_all_rating = document.getElementById('pl_all_rating');

            var rating_container = document.getElementById('rating_container');
            var div_rate_text = document.getElementById('div_rate_text');

            if (place.picture == null)
                img_container.src = "images/empty.jpg";
            else
                img_container.src = place.picture;

            pl_title.innerHTML = place.title;
            pl_addr.innerHTML = place.address;
            // pl_coords.innerHTML = 'lat: ' + place.latitude + ', lon: ' + place.longitude;
            if (place.vkData != null && place.fbData != null) {
                pl_vk_rating.innerHTML = place.vkData.rating + "<br/>" + place.vkData.popularity;
                pl_fb_rating.innerHTML = place.fbData.rating + "<br/>" + place.fbData.popularity;
                div_vk_rate.style.display='inherit';
                div_fb_rate.style.display='inherit';
                rating_container.style.width = '75%';
                div_rate_text.style.margin = '8% 0 0 0';
            }
            else if (place.vkData == null && place.fbData != null) {
                pl_fb_rating.innerHTML = place.fbData.rating + "<br/>" + place.fbData.popularity;
                div_vk_rate.style.display = 'none';
                div_fb_rate.style.display='inherit';
                rating_container.style.width = '57%';
                div_rate_text.style.margin = '11% 0 0 0';
            }
            else if (place.fbData == null && place.vkData != null) {
                pl_vk_rating.innerHTML = place.vkData.rating + "<br/>" + place.vkData.popularity;
                div_vk_rate.style.display='inherit';
                div_fb_rate.style.display = 'none';
                rating_container.style.width = '54%';
                div_rate_text.style.margin = '11% 0 0 0';
            }
            else if (place.fbData == null && place.vkData == null){
                div_vk_rate.style.display = 'none';
                div_fb_rate.style.display = 'none';
                rating_container.style.width = '45%';
                div_rate_text.style.margin = '11% 0 0 0';
            }
            pl_all_rating.innerHTML = place.overallRating + "<br/>" +
                                        place.overallPopularity;
        }
    })
}

function showLoading() {
    $(".loading").show();
}

function hideLoading() {
    $(".loading").hide();
}
