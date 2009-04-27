<!DOCTYPE html>
<% String version = (String)session.getAttribute("version");
// "version" is lazy loaded and will be empty until a request is made to the server
if (version==null){
	version="";
}
%>
<html>
<head>
<title>Wookie Widget Server <%=version%></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link type="text/css" href="/wookie/shared/js/jquery/themes/redmond/jquery-ui-1.7.1.custom.css" rel="stylesheet" />    
  <link type="text/css" href="../layout.css" rel="stylesheet" />
  <script type="text/javascript" src="/wookie/shared/js/jquery/jquery-1.3.2.min.js"></script>
  <script type="text/javascript" src="/wookie/shared/js/jquery/jquery-ui-1.7.custom.min.js"></script>
</head>

<body>
    <div id="header">
 		<div id="banner">
    		<div style="float:left;">
    			<img style="margin: 4 8px;" border="0" src="../shared/images/furry_white.png">
    		</div>
    		<div id="menu"></div>
    	</div> 
    	<div id="pagetitle">
    		<h3>Main Menu</h3>
    	</div>
    	<!--  END HEADER -->
	</div>
     
    <div id="content">    
	
	<% String errors = (String)session.getAttribute("error_value");%>
	<% String messages = (String)session.getAttribute("message_value");%>
	<%if(errors!=null){%>
      <p><img src="../shared/images/cancel.gif" width="16" height="16"><font color=red> <%=errors%> </font> </p>
	<%}%>
	<%if(messages!=null){%>
	<p><img src="../shared/images/greentick.gif" width="16" height="16">
		<font color=green>
		<%=messages%>
		</font>
	</p>
	<%}%>
	<h4>Options</h4>
	<br/>
	
	<div style="background:#ffffff;padding-top: 4px;">
	
	
	<div id="nifty">
					<b class="rtop">
						<b class="r1"></b>
						<b class="r2"></b>
						<b class="r3"></b>
						<b class="r4"></b>
					</b>	
						<div class="adminLayerDetail"><p><a href="WidgetWebMenuServlet?operation=LISTWIDGETS" ><img src="../shared/images/view_1.gif" width="20" height="20" border="0">&nbsp;View Widget Gallery</a></p></div>
					<b class="rbottom">
						<b class="r4"></b>
						<b class="r3"></b>
						<b class="r2"></b>
						<b class="r1"></b>
					</b>
	</div>
	<div id="spacer"></div>

	<div id="nifty">
					<b class="rtop">
						<b class="r1"></b>
						<b class="r2"></b>
						<b class="r3"></b>
						<b class="r4"></b>
					</b>	
						<div class="adminLayerDetail"><p><a href="../admin"><img src="../shared/images/unlock.gif" width="20" height="20" border="0">&nbsp;Administration menu</a></p></div>
					<b class="rbottom">
						<b class="r4"></b>
						<b class="r3"></b>
						<b class="r2"></b>
						<b class="r1"></b>
					</b>
	</div>
	<div id="spacer"></div>	
	
	<div id="nifty">
					<b class="rtop">
						<b class="r1"></b>
						<b class="r2"></b>
						<b class="r3"></b>
						<b class="r4"></b>
					</b>	
						<div class="adminLayerDetail"><p>
						<a href="WidgetWebMenuServlet?operation=INSTANTIATE"><img src="../shared/images/run.gif" width="18" height="18" border="0">&nbsp;Instantiate a widget</a></p></div>
					<b class="rbottom">
						<b class="r4"></b>
						<b class="r3"></b>
						<b class="r2"></b>
						<b class="r1"></b>
					</b>
	</div>
	<div id="spacer"></div>	
	
	<div id="nifty">
					<b class="rtop">
						<b class="r1"></b>
						<b class="r2"></b>
						<b class="r3"></b>
						<b class="r4"></b>
					</b>	
						<div class="adminLayerDetail"><p>
						<a href="requestapikey.jsp"><img src="../shared/images/mail.png" width="16" height="16" border="0">&nbsp;Request an API key for your application</a></p></div>
					<b class="rbottom">
						<b class="r4"></b>
						<b class="r3"></b>
						<b class="r2"></b>
						<b class="r1"></b>
					</b>
	</div>
	<div id="spacer"></div>	

</div></div>
	
<div id="footer">
	<div style="text-align:right">&nbsp;</div>
</div>

</body>
</html>
<% session.setAttribute("error_value", null); %>
<% session.setAttribute("message_value", null);%>

