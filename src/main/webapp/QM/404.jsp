<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>车来了</title>
    <meta name="apple-mobile-web-app-title" content="车来了">
    <meta name="description" content="最精准的实时公交App">
    <meta name="keywords" content="车来了，实时公交">

    <!--顺序不可以变，要不360的不使用极速的内核-->
    <meta name="renderer" content="webkit">
    <meta name="force-rendering" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">


    <meta name="HandheldFriendly" content="True">
    <meta name="MobileOptimized" content="320">
    <meta name="viewport" content="width=device-width,user-scalable=no,initial-scale=1,minimal-ui">

    <!-- uc强制竖屏 -->
    <meta name="screen-orientation" content="portrait">
    <!-- QQ强制竖屏 -->
    <meta name="x5-orientation" content="portrait">

    <!--下面两个不能合并写，要不浏览器就都不支持了-->
    <meta name="format-detection" content="telephone=no;">
    <meta name="format-detection" content="address=no;email=no;">

    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="white">
    <meta name="mobile-web-app-capable" content="yes">
    <meta http-equiv="cleartype" content="on">
    <style media="screen">
      body {
        background-color: #EEEEEE;
      }
      .container {
        width: 100%;
        height: 160px;
      }
      .img {
        background-image: url("./404.png");
        background-size: 100% 100%;
        width: 120px;
        height: 120px;
        margin: 0 auto 15px;
      }
      .desc {
        display: block;
        text-align: center;
        color: #666;
        font-size: 15px;
        height: 25px;
        line-height: 25px;
      }
      .hv-center {
        position: absolute;
        top: 40%;
        left: 50%;
        transform: translate(-50%, -50%);
      }
    </style>
  </head>
  <body>
    <div class="container hv-center">
      <div class="img"></div>
      <div class="desc">WOW!你跑到了外星啦~</div>
    </div>
<script>

  var isIOS,
      agent = navigator.userAgent;

  isIOS = /iP(ad|hone|od)/.test(agent);

  if (isIOS) {
    document.querySelector('body').style.background = '#EBEBF1';
  }

</script>
</body>
</html>
