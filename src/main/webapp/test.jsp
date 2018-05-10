<%@ page import="net.cloudstu.sg.entity.StockModel" %>
<%@ page import="net.cloudstu.sg.util.SpringUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="net.cloudstu.sg.dao.CookieDao" %>
<%@ page import="net.cloudstu.sg.dao.StockDao" %>
<%@ page import="net.cloudstu.sg.util.ShiPanEUtil" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>ok</title>
</head>
<body>
<%
    boolean flag = true;
    try {
        StockDao stockDao = SpringUtil.getBean(StockDao.class);
        StockModel stock = stockDao.selectByName("平安银行");
        if(stock==null) {
            flag = false;
        }
    }catch (Exception e) {
        e.printStackTrace();
        flag = false;
    }

    ShiPanEUtil.buy("000001", 100);

    if(flag) {
        out.print("test ok!");
    }


%>
</body>
</html>
