SELECT * FROM
  consultations
INNER JOIN patients ON consultations.patient_id = consultations.id
WHERE consultations.patient_id = ?
AND consultations.time > CURRENT_TIME();