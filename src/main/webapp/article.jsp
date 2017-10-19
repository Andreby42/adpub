<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.bus.chelaile.flow.model.ArticleInfo"%>
<%@	page import="java.util.ArrayList"%>
<%@	page import="java.util.List"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>车来了</title>
<meta name="description" content="最精准的实时公交App">
<meta name="apple-mobile-web-app-title" content="车来了">

<!--顺序不可以变，要不360的不使用极速的内核-->
<meta name="renderer" content="webkit">
<meta name="force-rendering" content="webkit">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">


<meta name="HandheldFriendly" content="True">
<meta name="MobileOptimized" content="320">
<meta name="viewport"
	content="width=device-width,user-scalable=no,initial-scale=1,minimal-ui">

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
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="cleartype" content="on">

<style>
html, body {
	width: 100%;
	overflow-x: hidden;
}

body {
	background-color: #ebebf1;
	font-family: PingFangSC, Helvetica, Arial, sans-serif;
	margin: 0;
}

.page {
	padding: 20px 15px 15px;
	background-color: #fff;
}

.title {
	font-size: 20px;
	font-weight: 600;
	color: #333;
}

.author-and-time {
	margin: 5px 0 30px;
	font-size: 13px;
	line-height: 1;
	color: #aaa;
}

.img-container {
	width: 100%;
	height: 150px;
	border-radius: 2px;
	background: no-repeat center #fff;
	background-size: 72px 72px;
	position: relative;
}

img {
	border: none;
	outline: none;
	width: 100%;
}

.img-container img {
	position: absolute;
	height: 100%;
	border-radius: 2px;
}

p {
	font-size: 17px;
	line-height: 1.53;
	color: #333333;
}

.footer {
	display: none;
	position: fixed;
	bottom: -1px;
	width: 100%;
	height: 59px;
	z-index: 5;
	background-color: rgba(0, 0, 0, .85);
	color: #f2f2f2;
}

.footer-left {
	position: relative;
	display: block;
	height: 100%;
	overflow: hidden;
	padding-left: 62px;
}

.ico-logo {
	background-image: url(./logo.png);
	background-size: 44px 44px;
	width: 44px;
	height: 44px;
	position: absolute;
	top: 7px;
	left: 12px;
}

.footer-title {
	font-weight: 400;
	margin: 8px 0 0 0;
	line-height: 24px;
	font-size: 16px;
}

.footer-desc {
	line-height: 16px;
	font-size: 13px;
}

.common-flex {
	display: -webkit-box;
	display: -ms-flexbox;
	display: flex;
	display: -webkit-flex;
	-webkit-box-orient: horizontal;
	-webkit-box-direction: normal;
	-ms-flex-direction: row;
	flex-direction: row;
	-webkit-flex-direction: row;
	-webkit-box-pack: justify;
	-ms-flex-pack: justify;
	justify-content: space-between;
	-webkit-justify-content: space-between
}

.footer-right {
	position: absolute;
	top: 14px;
	right: 6px;
	width: 116px;
	z-index: 2;
}

.btn {
	display: inline-block;
	width: 80px;
	line-height: 30px;
	text-align: center;
	background: #2498db;
	color: #fff;
}

.ico-close {
	background: url("./close_white.png") no-repeat center;
	background-size: 12px 12px;
	width: 30px;
	height: 30px;
}

a {
	user-select: none;
	outline: none !important;
	text-decoration: none;
	cursor: pointer;
}

.recommend-warp {
	margin: 10px 0;
	background-color: #fff;
}

.recommend-title {
	width: 100%;
	height: 44px;
	line-height: 44px;
	padding-left: 50px;
	background-color: #fbfbfd;
	position: relative;
	font-size: 14px;
	color: #666;
}

.ico-recommend {
	background: url("./recommend.png") no-repeat;
	background-size: 100%;
	width: 24px;
	height: 24px;
	position: absolute;
	left: 15px;
	top: 10px;
}

.recommend-card {
	width: 100%;
	-webkit-box-sizing: border-box;
	box-sizing: border-box;
	-webkit-tap-highlight-color: transparent;
	padding: 15px;
	position: relative;
}

.bb-1 {
	height: 1px;
	background: #ddd;
	margin-left: 15px;
	margin-right: 15px;
}

.recommend-card-title {
	font-size: 17px;
	line-height: 1.4;
	color: #333;
	overflow: hidden;
	text-overflow: ellipsis;
}

.has-1-img {
	height: 100px;
	-webkit-box-align: start;
	-ms-flex-align: start;
	align-items: flex-start;
	-webkit-align-items: flex-start
}

.has-1-img-right {
	-webkit-box-flex: 0;
	-ms-flex: 0 0 auto;
	flex: 0 0 auto;
	-webkit-flex: 0 0 auto;
	width: 32%;
	height: 70px;
	border-radius: 2px;
	background: #eee no-repeat center;
	background-size: cover;
}

.has-3-img {
	height: 160px;
}

.has-1-img .recommend-card-title {
	height: 48px;
	margin-right: 15px;
	word-wrap: break-word;
	width: 64%;
	-webkit-box-flex: 0;
	-ms-flex-positive: 0;
	flex-grow: 0;
	-ms-flex-negative: 2;
	flex-shrink: 2
}

.has-3-img .recommend-card-title {
	height: 24px;
	white-space: nowrap;
	width: 100%;
}

.has-3-img .thumbnails-container {
	margin-top: 10px;
	height: 72px;
}

.has-3-img .img-block {
	width: 33.3%;
	border-radius: 2px;
	background: #eee no-repeat center;
	background-size: cover;
}

.m-5 {
	margin: 0 5px;
}

.recommend-author-and-time {
	position: absolute;
	left: 15px;
	bottom: 15px;
	font-size: 11px;
	color: #aaa;
}
</style>
</head>

<body>
	<!-- 获取数据 -->
	<%
		ArticleInfo articleInfo = (ArticleInfo) request.getAttribute("articleInfo");
		
		List<ArticleInfo> recomArticles = (List<ArticleInfo>) request.getAttribute("recomArticles");
		ArrayList<String> urls = new ArrayList<String>();
		if(recomArticles != null && recomArticles.size() > 0) {
			for(ArticleInfo ar : recomArticles) {
		urls.add("'" + ar.getUrl() + "'");
			}
		}
	%>
	<!-- 主页 -->
	<input id="hidzy" type="hidden" value="<%=articleInfo.getShareDesc()%>">
	<div class="page">
		<div class="title"><%=articleInfo.getTitle()%></div>
		<div class="author-and-time">
			<span class="author"><%=articleInfo.getAuthor()%></span>&nbsp;<span
				class="time"><%=articleInfo.getDesc()%></span>
		</div>
		<!-- 主体 -->
		<div>
			<!-- 内容和图片 -->
			<%=articleInfo.getContent()%>
		</div>
	</div>


	<%
		if(urls.size() > 0) 
			{
	%>

	<!-- 相关推荐 -->
	<div class="recommend-warp">
		<div class="recommend-title">
			<div class="ico-recommend"></div>
			相关推荐
		</div>
		<%
			int total = recomArticles.size();
				for (int i = 0; i < total && i < 3; i++) { //控制推荐文章数目
			if(recomArticles.get(i).getImgUrl().size() < 3) {	//控制样式，只有单图和三图模式
		%>
		<div class="recommend-card common-flex has-1-img"
			onclick="jumpNewPage(<%=i%>)">
			<div class="recommend-card-title">
				<%=recomArticles.get(i).getTitle()%>
			</div>
			<div class="has-1-img-right"
				style="background-image: url(<%=recomArticles.get(i).getImgUrl().get(0)%>);"></div>
			<div class="recommend-author-and-time"><%=recomArticles.get(i).getAuthor()%>
				<%=recomArticles.get(i).getDesc()%></div>
		</div>
		<%
			if(i != 2 && i < total - 1)  {
		%>
		<div class="bb-1"></div>
		<%
			}
		%>

		<%
			} else {
		%>
		<div class="recommend-card has-3-img" onclick="jumpNewPage(<%=i%>)">
			<div class="recommend-card-title">
				<%=recomArticles.get(i).getTitle()%>
			</div>
			<div class="thumbnails-container common-flex">
				<div class="img-block"
					style="background-image: url(<%=recomArticles.get(i).getImgUrl().get(0)%>);"></div>
				<div class="img-block m-5"
					style="background-image: url(<%=recomArticles.get(i).getImgUrl().get(1)%>);"></div>
				<div class="img-block"
					style="background-image: url(<%=recomArticles.get(i).getImgUrl().get(2)%>);"></div>
			</div>
			<div class="recommend-author-and-time"><%=recomArticles.get(i).getAuthor()%>
				<%=recomArticles.get(i).getDesc()%></div>
		</div>
		<%
			if(i != 2 && i < total - 1)  {
		%>
		<div class="bb-1"></div>
		<%
			}
		%>
		<%
			}
				}
		%>
	</div>
	<%
		}
	%>


	<div class="footer">
		<div class="footer-left">
			<div class="ico-logo"></div>
			<div class="footer-title f-16 lh-24">车来了</div>
			<div class="footer-desc f-13 lh-16">精准的实时公交App</div>
		</div>
		<div class="footer-right  common-flex">
			<a href="//dlink.chelaile.net.cn/download?wxckey=CK1298659594724"
				class="btn">立即下载</a>
			<div class="ico-close" onclick="closeFooter()"></div>
		</div>
	</div>
</body>
<script>
    function setDefaultImage() {
        var isIOS = /iP(ad|hone|od)/.test(navigator.userAgent),
        	isChelaile = /Chelaile/i.test(navigator.userAgent),
            $imgCon = document.querySelectorAll('.img-container'),
            len = $imgCon.length,
            imgRtio = <%=articleInfo.getImgRtio()%>, // 坤坤来填写，图片的 高/宽 的值 [123,123,12312,3]
            ww = document.body.clientWidth - 30; // 图片的定宽
        
        if (!isChelaile) {
            document.querySelector('.footer').style.display = 'block';
            document.querySelector('body').style.paddingBottom = '60px';
        }

        for (var i = 0; i < len; i++) {
            if (isIOS) {
                $imgCon[i].style.backgroundImage = 'url(pic_ios.png)';
                $imgCon[i].style.backgroundColor = '#ebebf1';
            }
            else {
                $imgCon[i].style.backgroundImage = 'url(pic_android.png)';
                $imgCon[i].style.backgroundColor = '#eeeeee';
            }

            // 根据原图比例设置图片大小
            if (imgRtio[i]) {
              $imgCon[i].style.height = ww * imgRtio[i] + 'px';
            }
        }
    }

    setTimeout(setDefaultImage, 0);
    
    document.title = '<%=articleInfo.getTitle()%>' ;

	function closeFooter() {
		document.querySelector('.footer').style.display = 'none';
		document.querySelector('body').style.paddingBottom = '0px';
	}

	var newsHref = <%=urls%>;

	function jumpNewPage(index) {
		location.href = newsHref[index];
	}
</script>
</html>