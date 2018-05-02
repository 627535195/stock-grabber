<%@ page import="net.cloudstu.sg.util.SpringUtil" %>
<%@ page import="net.cloudstu.sg.dao.StockDao" %>
<%@ page import="net.cloudstu.sg.entity.StockModel" %>
<%@ page import="java.util.List" %>
<%@ page import="net.cloudstu.sg.grab.ScreamStockRepo" %>
<%@ page import="java.util.stream.Collectors" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>ok</title>
</head>
<body>
ok 8888!

<%
    StockDao stockDao = SpringUtil.getBean(StockDao.class);
    List<StockModel> monitoredStocks = stockDao.selectMonitored();

    for(StockModel stock : monitoredStocks) {
        ScreamStockRepo.codes.add(stock.getCode());
    }

    System.out.println(ScreamStockRepo.codes.size());
%>
</body>
</html>
