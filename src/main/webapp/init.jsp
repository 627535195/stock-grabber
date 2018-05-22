<%@ page import="net.cloudstu.sg.util.SpringUtil" %>
<%@ page import="net.cloudstu.sg.dao.StockDao" %>
<%@ page import="net.cloudstu.sg.entity.StockModel" %>
<%@ page import="java.util.List" %>
<%@ page import="net.cloudstu.sg.grab.ScreamStockRepo" %>
<%@ page import="java.util.stream.Collectors" %>
<%@ page import="net.cloudstu.sg.util.ShiPanEUtil" %>
<%@ page import="net.cloudstu.sg.grab.StockHoldRepo" %>
<%@ page import="net.cloudstu.sg.grab.MonitoredStockLoader" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>ok</title>
</head>
<body>
ok 8888!

<%
    //                测试交易接口
    ShiPanEUtil.buy("000001", 100);
    StockHoldRepo.refreshHoldStocks();
    MonitoredStockLoader.load();
    out.print("初始化涨停预测完成！");
%>
</body>
</html>
