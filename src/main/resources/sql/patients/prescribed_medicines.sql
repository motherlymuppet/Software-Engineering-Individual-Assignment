SELECT * FROM
  prescribed_medicines
INNER JOIN medicines ON prescribed_medicines.medicine_id = medicines.id
WHERE prescribed_medicines.patient_id = ?