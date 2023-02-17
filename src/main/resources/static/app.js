var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#inference").show();
        $("#chooseImgLabel").css("display", "block");
        $("#disconnect").css("display", "block");
        $("#connect").css("display", "none");
    }
    else {
        $("#inference").hide();
        $("#chooseImgLabel").css("display", "none");
         $("#disconnect").css("display", "none");
         $("#connect").css("display", "block");
         $("#prob").html("");
    }
    $("#predictions").html("");
}

function connect() {
    var socket = new SockJS('/model-inference-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/prediction/response', function (prediction) {
            showPrediction(
                prediction
             );
        });
        //enable predictor_modal, disconnect and upload buttons
        $("#disconnect").prop('disabled', false);
        $("#upload").prop('disabled', false);
        $("#predictor_modal").prop('disabled', false);
        //disable connect button
        $("#connect").prop('disabled', true);
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");

    //disable predictor_modal, disconnect and upload buttons
    $("#disconnect").prop('disabled', true);
    $("#upload").prop('disabled', true);
    $("#predictor_modal").prop('disabled', true);
    //enable connect button
    $("#connect").prop('disabled', false);
    $("#connect").css("display", "block");
    $("#disconnect").css("display", "none");
    $("#chooseImgLabel").css("display", "none");
    $("#prob").html("");
}

function showPrediction(prediction) {
    var trows = '';
    var predObj = JSON.parse(prediction.body);
    var num = 1;
    for(key in predObj){
        console.log(num +" "+key +" : "+predObj[key]);
        var temp = ''; //'<tr><th scope="row">'+num+'</th><td>'+key+'</td><td>'+predObj[key]+'</td></tr>';
        if(predObj[key] < 0.5){
            temp =  '<tr><th scope="row">'+num+'</th><td>'+key+'</td><td>'+predObj[key]+'</td></tr>';
        }else{
            temp =  '<tr class="table-danger"><th scope="row">'+num+'</th><td>'+key+'</td><td>'+predObj[key]+'</td></tr>';
        }
        trows = trows+''+temp;
        num++;
    }
    var prob_head = '<h2 class="featurette-heading">Results for the Analysis<span class="text-muted"></span></h2>';
    $("#prob-heading").html(prob_head);
    var html_content = '<div class="table-responsive-sm"><table class="table"><thead class="thead-dark"><tr><th scope="col">#</th><th scope="col">Disease/Pest</th><th scope="col">Probability</th></tr></thead><tbody>'+trows+'</tbody></table></div>';
    $("#prob").html(html_content);
};

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#upload" ).click(function(){
        //alert("Upload button clicked!");
        var fd = new FormData();
        var files = $('#file')[0].files[0];
        fd.append('file', files);
        $.ajax({
            url: '/api/v1/prediction/socket/foliage',
            type: 'post',
            data: fd,
            contentType: false,
            processData: false,
            success: function(response){
                if(response.success === true){
                    //TODO
                   //alert('file uploaded');
                }
                else{
                    //TODO
                    //alert('file not uploaded');
                }
            },
        });
    });
});

var loadFile = function(event) {
    var image = document.getElementById('output');
    image.src = URL.createObjectURL(event.target.files[0]);
    document.getElementById('prob').innerHTML = '<button class="btn btn-outline-primary" type="button" id="upload" href="#" onclick="uploadFile()" ><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-upload" viewBox="0 0 16 16"><path d="M.5 9.9a.5.5 0 0 1 .5.5v2.5a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1v-2.5a.5.5 0 0 1 1 0v2.5a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2v-2.5a.5.5 0 0 1 .5-.5z"/><path d="M7.646 1.146a.5.5 0 0 1 .708 0l3 3a.5.5 0 0 1-.708.708L8.5 2.707V11.5a.5.5 0 0 1-1 0V2.707L5.354 4.854a.5.5 0 1 1-.708-.708l3-3z"/></svg>Upload</button>';
    $("#head-title-ai").css("display", "none");
    $("#head-content-ai").css("display", "none");
};

var uploadFile = function(){
     //alert("Upload button clicked!");
     document.getElementById('prob').innerHTML = '<div class="text-center"><div class="spinner-border" role="status"></div><br/><strong>Processing...</strong></div>';
     var fd = new FormData();
     var files = $('#file')[0].files[0];
     fd.append('file', files);
     $.ajax({
         url: '/api/v1/prediction/socket/foliage',
         type: 'post',
         data: fd,
         contentType: false,
         processData: false,
         success: function(response){
             if(response.success === true){
                 //TODO
                //alert('file uploaded');
             }
             else{
                 //TODO
                 //alert('file not uploaded');
             }
         },
     });
};