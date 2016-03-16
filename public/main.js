function getPhotos(photosData){
    for(var i in photosData){
        var elem = $("<img>");
        elem.attr("src", photosData[i].filename);
        $("#photos").append(elem);
    }
}

function getUser(response){
    if(response.length == 0){
        $("#login").show();
    }else{
        $("#upload").show();
        $.get("/photos", getPhotos);
    }
}

$.get("/user", getUser);