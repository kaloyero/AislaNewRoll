<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <title>Roll It Ejemplo</title>
		<script src="http://code.jquery.com/jquery-latest.js"></script>
	<script>
	
	$( document ).ready(function() {
	
                $.ajax({
                    type: 'GET',
                    url:'http://localhost:8080/sv/enviar',
                    //contentType : "application/json",
                    //data :({info : "me"}),
        			data :  ({info : "{'fields':[],'sessionId':'session00002','i36Session':'','aidKey':{'cursorY':'6','cursorX':'52','value':'33554432'}}"}),
        		    //dataType: 'json',
        			//data :  ('{"fields":[{"attributes":"HostField|2097444|10|5|52","cursorX":53,"cursorY":6,"length":"10","data":"LROLAN    "},{"attributes":"HostField|2097447|10|6|52","cursorX":53,"cursorY":7,"length":"10","data":"LROLAN    "},{"attributes":"HostField|292|10|7|52","cursorX":53,"cursorY":8,"length":"10","data":"          "},{"attributes":"HostField|292|10|8|52","cursorX":53,"cursorY":9,"length":"10","data":"          "},{"attributes":"HostField|292|10|9|52","cursorX":53,"cursorY":10,"length":"10","data":"          "}],"sessionId":"session00002","i36Session":"","aidKey":{"cursorY":"6","cursorX":"52","value":"33554432"}}'),

                   // url: 'http://localhost:8080/DanmarWeb/articulo/findAll/' + start + '/' + params.endRow,

                    success: function(data) {
                        console.log("DATADO",data)

                    }
                });
          
        });

	
	
	
	
	</script>
</head>

<body

	
	  <!-- endbuild -->
</body>
</html>
