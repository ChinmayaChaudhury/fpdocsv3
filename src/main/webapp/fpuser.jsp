<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="content-language" content="en">
<link rel="shortcut icon" href="img/icons/share_document.ico" type="image/x-icon" sizes="16x16">
<link rel="icon" href="img/icons/share_document.ico" type="image/x-icon" sizes="16x16">
<title>FairPrice Document Service</title>
<script 
    src="https://sapui5.hana.ondemand.com/resources/sap-ui-core.js"
    id="sap-ui-bootstrap" 
    data-sap-ui-compactVersion="edge"
	data-sap-ui-xx-bindingSyntax = "complex"  
	data-sap-ui-resourceroots='{
	  "sap.docs.fpuser":"./components/fpuser",
	  "view.fpuser":"./view/fpuser",
	  "view.all":"./view/all",
	  "util":"./util",
	  "controller.fpuser":"./controller/fpuser",
	  "com.sap.fp.docrepo":"./" 
	}'
	data-sap-ui-libs="sap.m,sap.ui.ux3, sap.ui.commons, sap.ui.table, sap.ui.core"
	data-sap-ui-theme="sap_bluecrystal"
	>
</script> 

<!-- Create the unified shell default renderer control and add it to the page -->
<script type="text/javascript">
   
	sap.ui.getCore().attachInit(function() {
		new sap.m.Shell({
			app: new sap.ui.core.ComponentContainer({
				height : "100%",
				name : "sap.docs.fpuser"
			})
		}).placeAt("content");
	});

</script> 
<link rel="stylesheet" type="text/css" href="css/main.css">
</head>
<body class="sapUiBody" role="application"> 
	<div id="content" ></div>
</body>
