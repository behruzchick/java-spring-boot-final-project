
## API Reference

#### Authorization by Employee

```http
  GET /api/employee/signIn

```
#### Get all items

```http
  GET /api/employee/getAllEmployees
  GET /api/employee/getAllClients
  GET /api/employee/getAllAds

```

#### Add employee (Add employee with director role on your database)

```http
  POST /api/employee/addEmployee
```

#### Employee methods

```http
  GET /api/employee/findEmployee/{id}
  PUT /api/employee/editEmployee/{id}
  DELETE /api/employee/deleteEmployee/{id}
```

#### CLient methods

```http
  GET /api/client/findEmployee/{id}
  POST /api/client/addClient
  PUT /api/client/updateClient/{id}
  PATCH /api/client/addArchive/{id}
```


#### Advertisement   methods

```http
  GET /api/ad/getAd/{id}
  POST /api/ad/createAd
  PUT /api/ad/updateAd/{id}
  DELETE /api/employee/deleteAd/{id}
  PUT /api/employee/stopAd/{id}
```


#### Filter methods (Statistics)

```http
  GET /api/statistics/daily_registered_clients/
  GET /api/statistics/monthly_registered_clients/
  GET /api/statistics/best_employees/
  GET /api/statistics/best_employee/
  GET /api/statistics/getEmployeesByRole/{role_name}
  GET /api/statistics/getEmployeesByAge/{age}
  GET /api/statistics/getAdvertisementPriceByType/{price}
  GET /api/statistics/getAdvertisementMostAddedByEmployee
  GET /api/statistics/getMonthlyIncludedAdvertisement
  GET /api/statistics/getMonthlyStoppedAdvertisement
```

Project was stopped by reason banned license in Intelej idea


