<%@ page language="java" contentType="text/html; charset=UTF-8"
        pageEncoding="UTF-8"%>
<html>
<body>
<h2>Hello World!</h2>
功能列表
<br>
<a href="<%=request.getContextPath()%>/parent/login/index.htm">家长-登陆</a>
<br>

<br>
<a href="<%=request.getContextPath()%>/teacher/login/index.htm">教师-登陆</a>
<br>
<a href="<%=request.getContextPath()%>/client/teacher/uploadHeadImg.htm">学校管理-登陆</a>


   <form method="post" action="<%=request.getContextPath()%>/client/teacher/transmitMedia.htm" enctype="multipart/form-data">  
            <input type="text" name="token" value="1234567"/>
             <input type="text" name="child_ids" value="1,2,3"/>
             <input type="text" name="msgID" value="13"/>
              <input type="text" name="mediaType" value="3"/>
             
               <input type="file" name="files"/>  
  <input type="file" name="files"/>  
  
            <input type="file" name="files"/>  
            <input type="submit"/>  
        </form> 


</body>
</html>