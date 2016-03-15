function getUser(userData){
    if(userData.length == 0){
        $("#login").show();
    }
}

$.get("/user", getUser);