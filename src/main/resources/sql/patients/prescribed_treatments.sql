SELECT * FROM
  prescribed_treatments
INNER JOIN treatments ON prescribed_treatments.treatment_id = treatments.id
WHERE prescribed_treatments.patient_id = ?