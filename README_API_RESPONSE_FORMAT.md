# Standard API response format

API responses use exactly these four top-level fields: `success`, `message`, `data`, and `error`.

## Success response

```json
{
  "success": true,
  "message": "LOS dashboard data fetched successfully",
  "data": { },
  "error": null
}
```

## Error response

```json
{
  "success": false,
  "message": "fromDate must be on or before toDate",
  "data": null,
  "error": "fromDate must be on or before toDate"
}
```

## Rate-limit response

When the per-endpoint rate limit is exceeded, the API returns HTTP `429 Too Many Requests` with the same four top-level fields. `error` contains rate-limit details.

Note: `ApiResponse` is shared by existing endpoints that already use it, so those endpoints will also serialize the new `error` field. Dashboard success payloads remain nested under `data`.
