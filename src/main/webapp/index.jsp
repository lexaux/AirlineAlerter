<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>

<head>
    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
    <script type="text/javascript" src="scripts/jquery/jquery-1.9.1.min.js"></script>
    <script type="text/javascript">

        // Load the Visualization API and the piechart package.
        google.load('visualization', '1.0', {'packages': ['corechart']});

        //        // Set a callback to run when the Google Visualization API is loaded.
        //        google.setOnLoadCallback(drawChart);

        // Callback that creates and populates a data table,
        // instantiates the pie chart, passes in the data and
        // draws it.
        function drawChart(data) {

            // Create the data table.
            var dataTable = new google.visualization.DataTable();

            dataTable.addColumn('date', 'Date');
            dataTable.addColumn('number', 'Price');

            dataTable.addRows(data);


            // Set chart options
            var options = {'title': 'Wizzair ticket prices for Kiev-Budapest 29 April',
                'width': 1024,
                'height': 768};

            // Instantiate and draw our chart, passing in some options.
            var chart = new google.visualization.LineChart(document.getElementById('chart_div'));
            chart.draw(dataTable, options);
        }

        $(document).ready(function () {
            var request = $.ajax({
                url: "data.jsp"
            });
            request.done(function (d) {
                var ret = $.map(d, function (element, index) {
                    var e = element.slice();
                    if (index != 0) {
                        e[0] = new Date(element[0]);
                    }
                    return [e];
                });
                drawChart(ret)
            });
            request.fail(function (d) {
                alert('Error loading data' + d)
            });
        })

    </script>
</head>
<body>
<div id="chart_div"></div>
<h2>Wizzair recordings:</h2>
</body>
</html>
