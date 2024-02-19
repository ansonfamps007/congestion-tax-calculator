**Congestion Tax Calculator**

Steps to run 

1) Import project to eclipse or Intellij as spring boot gradle project
2) Use Jdk 17 as JRE
3) Run main class CongestionTaxCalculatorApplication
4) Use POSTMAN POST request along with the URL http://localhost:8080/api/v1/congestions/tax with the input body. For example:

{
"vehicle": "van",
"dates": [
"2013-02-08 06:27:00",
"2013-02-08 06:20:27",
"2013-02-08 06:31:27",
"2013-02-06 14:35:00",
"2013-02-07 15:29:00",
"2013-02-08 15:47:00",
"2013-02-08 16:01:00"
]
}" 

to submit the request.
