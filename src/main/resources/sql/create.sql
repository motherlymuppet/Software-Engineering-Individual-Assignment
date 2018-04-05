-- http://webarchive.nationalarchives.gov.uk/+/http://www.cabinetoffice.gov.uk/media/254290/GDS%20Catalogue%20Vol%202.pdf

CREATE TABLE practices (
  id   BIGINT      NOT NULL AUTO_INCREMENT,
  name VARCHAR(35) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE risk_categories (
  id   BIGINT      NOT NULL AUTO_INCREMENT,
  name VARCHAR(35) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE conditions (
  id   BIGINT       NOT NULL AUTO_INCREMENT,
  name VARCHAR(250) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE consultants (
  id      BIGINT      NOT NULL AUTO_INCREMENT,
  title   VARCHAR(35) NOT NULL,
  surname VARCHAR(35) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE treatments (
  id   BIGINT       NOT NULL AUTO_INCREMENT,
  name VARCHAR(250) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE medicines (
  id   BIGINT       NOT NULL AUTO_INCREMENT,
  name VARCHAR(250) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE patients (
  id                    BIGINT      NOT NULL AUTO_INCREMENT,
  surname               VARCHAR(35) NOT NULL,
  first_name             VARCHAR(35) NOT NULL,
  address_1             VARCHAR(35) NOT NULL,
  address_2             VARCHAR(35) NOT NULL,
  address_3             VARCHAR(35) NULL,
  address_4             VARCHAR(35) NULL,
  address_5             VARCHAR(35) NULL,
  date_of_birth         DATE        NOT NULL,
  phone                 VARCHAR(12) NOT NULL,
  practice_id           BIGINT      NOT NULL REFERENCES practices (id) ON DELETE RESTRICT ON UPDATE CASCADE,
  risk_category_id      BIGINT      NOT NULL REFERENCES risk_categories (id) ON DELETE RESTRICT ON UPDATE CASCADE,
  consultant_id         BIGINT      NOT NULL REFERENCES consultants (id) ON DELETE RESTRICT ON UPDATE CASCADE,

  next_of_kin_surname   VARCHAR(35) NOT NULL,
  next_of_kin_first_name VARCHAR(35) NOT NULL,
  next_of_kin_phone     VARCHAR(12) NOT NULL,

  PRIMARY KEY (id)
);

CREATE TABLE diagnoses (
  patient_id   BIGINT NOT NULL REFERENCES patients (id) ON DELETE CASCADE ON UPDATE CASCADE,
  condition_id BIGINT NOT NULL REFERENCES conditions (id) ON DELETE RESTRICT ON UPDATE CASCADE,
  PRIMARY KEY (patient_id, condition_id)
);

CREATE TABLE prescribed_treatments (
  patient_id   BIGINT NOT NULL REFERENCES patients (id) ON DELETE CASCADE ON UPDATE CASCADE,
  treatment_id BIGINT NOT NULL REFERENCES treatments (id) ON DELETE RESTRICT ON UPDATE CASCADE,
  PRIMARY KEY (patient_id, treatment_id)
);

CREATE TABLE prescribed_medicines (
  patient_id  BIGINT NOT NULL REFERENCES patients (id) ON DELETE CASCADE ON UPDATE CASCADE,
  medicine_id BIGINT NOT NULL REFERENCES medicines (id) ON DELETE RESTRICT ON UPDATE CASCADE,
  PRIMARY KEY (patient_id, medicine_id)
);

CREATE TABLE consultations (
  id BIGINT NOT NULL AUTO_INCREMENT,
  time TIMESTAMP NOT NULL,
  practice_id BIGINT NOT NULL REFERENCES practices (id) ON DELETE RESTRICT ON UPDATE CASCADE,
  consultant_id BIGINT NOT NULL REFERENCES consultants (id) ON DELETE RESTRICT ON UPDATE CASCADE,
  patient_id BIGINT NOT NULL REFERENCES patients (id) ON DELETE CASCADE ON UPDATE CASCADE,
  PRIMARY KEY (id)
);

INSERT INTO practices (name) VALUES ('Claypath');
INSERT INTO practices (name) VALUES ('Langley Park');
INSERT INTO practices (name) VALUES ('Langley Moor');
INSERT INTO practices (name) VALUES ('Chester-le-Street');

INSERT INTO risk_categories (name) VALUES ('Low');
INSERT INTO risk_categories (name) VALUES ('Medium');
INSERT INTO risk_categories (name) VALUES ('High');

INSERT INTO conditions (name) VALUES ('Schizophrenia');
INSERT INTO conditions (name) VALUES ('Bipolar Disorder');
INSERT INTO conditions (name) VALUES ('Obsessive Compulsive Disorder');
INSERT INTO conditions (name) VALUES ('Anxiety');
INSERT INTO conditions (name) VALUES ('Depression');
INSERT INTO conditions (name) VALUES ('Anorexia');
INSERT INTO conditions (name) VALUES ('ADHD');
INSERT INTO conditions (name) VALUES ('Autism');

INSERT INTO consultants (title, surname) VALUES ('Mr', 'Longstaff');
INSERT INTO consultants (title, surname) VALUES ('Mr', 'Sanjay');
INSERT INTO consultants (title, surname) VALUES ('Ms', 'Borrowdale');
INSERT INTO consultants (title, surname) VALUES ('Ms', 'Naylor');

INSERT INTO treatments (name) VALUES ('Psychotherapy');
INSERT INTO treatments (name) VALUES ('CBT');
INSERT INTO treatments (name) VALUES ('Counselling');
INSERT INTO treatments (name) VALUES ('ECT');

INSERT INTO medicines (name) VALUES ('Risperdal');
INSERT INTO medicines (name) VALUES ('Diazepam');
INSERT INTO medicines (name) VALUES ('Melatonin');
INSERT INTO medicines (name) VALUES ('Dopamine');
INSERT INTO medicines (name) VALUES ('Depakene');
INSERT INTO medicines (name) VALUES ('Methylphenidate');

INSERT INTO patients (
  surname,
  first_name,
  address_1,
  address_2,
  address_3,
  address_4,
  address_5,
  date_of_birth,
  phone,
  practice_id,
  risk_category_id,
  consultant_id,
  next_of_kin_surname,
  next_of_kin_first_name,
  next_of_kin_phone
) VALUES (
  'Black',
  'Fred',
  '1 South Street',
  'Durham',
  'DH1 1LE',
  NULL,
  NULL,
  '1999-01-01',
  '0712312229',
  1,
  2,
  1,
  'Black',
  'Bill',
  '01913341712'
);

INSERT INTO diagnoses (patient_id, condition_id) VALUES (1, 1);
INSERT INTO diagnoses (patient_id, condition_id) VALUES (1, 2);
INSERT INTO prescribed_treatments (patient_id, treatment_id) VALUES (1, 1);
INSERT INTO prescribed_treatments (patient_id, treatment_id) VALUES (1, 2);
INSERT INTO prescribed_medicines (patient_id, medicine_id) VALUES (1, 1);

INSERT INTO patients (
  surname,
  first_name,
  address_1,
  address_2,
  address_3,
  address_4,
  address_5,
  date_of_birth,
  phone,
  practice_id,
  risk_category_id,
  consultant_id,
  next_of_kin_surname,
  next_of_kin_first_name,
  next_of_kin_phone
) VALUES (
  'Brown',
  'Bill',
  '2 South Street',
  'Durham',
  'DH1 1LE',
  NULL,
  NULL,
  '2000-02-02',
  '0765432122',
  1,
  3,
  2,
  'Brown',
  'Jane',
  '01913341734'
);

INSERT INTO diagnoses (patient_id, condition_id) VALUES (2, 3);
INSERT INTO diagnoses (patient_id, condition_id) VALUES (2, 4);
INSERT INTO prescribed_treatments (patient_id, treatment_id) VALUES (2, 3);
INSERT INTO prescribed_medicines (patient_id, medicine_id) VALUES (2, 2);
INSERT INTO prescribed_medicines (patient_id, medicine_id) VALUES (2, 3);

INSERT INTO patients (
  surname,
  first_name,
  address_1,
  address_2,
  address_3,
  address_4,
  address_5,
  date_of_birth,
  phone,
  practice_id,
  risk_category_id,
  consultant_id,
  next_of_kin_surname,
  next_of_kin_first_name,
  next_of_kin_phone
) VALUES (
  'Green',
  'Mary',
  '10 North Street',
  'Langley Park',
  'Durham',
  'DH7 9ED',
  NULL,
  '1990-04-04',
  '0789765432',
  2,
  3,
  3,
  'Green',
  'John',
  '7898789867'
);

INSERT INTO diagnoses (patient_id, condition_id) VALUES (3, 4);
INSERT INTO diagnoses (patient_id, condition_id) VALUES (3, 5);
INSERT INTO diagnoses (patient_id, condition_id) VALUES (3, 6);
INSERT INTO prescribed_treatments (patient_id, treatment_id) VALUES (3, 3);
INSERT INTO prescribed_treatments (patient_id, treatment_id) VALUES (3, 4);
INSERT INTO prescribed_medicines (patient_id, medicine_id) VALUES (3, 2);
INSERT INTO prescribed_medicines (patient_id, medicine_id) VALUES (3, 4);

INSERT INTO patients (
  surname,
  first_name,
  address_1,
  address_2,
  address_3,
  address_4,
  address_5,
  date_of_birth,
  phone,
  practice_id,
  risk_category_id,
  consultant_id,
  next_of_kin_surname,
  next_of_kin_first_name,
  next_of_kin_phone
) VALUES (
  'Gray',
  'Joe',
  '12 East Road',
  'Langley Moor',
  'Durham',
  'DH4 2BV',
  NULL,
  '1980-05-05',
  '0123456778',
  3,
  3,
  1,
  'Gray',
  'Betty',
  '01913766963'
);

INSERT INTO diagnoses (patient_id, condition_id) VALUES (4, 2);
INSERT INTO diagnoses (patient_id, condition_id) VALUES (4, 5);
INSERT INTO prescribed_treatments (patient_id, treatment_id) VALUES (4, 1);
INSERT INTO prescribed_medicines (patient_id, medicine_id) VALUES (4, 5);

INSERT INTO patients (
  surname,
  first_name,
  address_1,
  address_2,
  address_3,
  address_4,
  address_5,
  date_of_birth,
  phone,
  practice_id,
  risk_category_id,
  consultant_id,
  next_of_kin_surname,
  next_of_kin_first_name,
  next_of_kin_phone
) VALUES (
  'Red',
  'Susan',
  'Ashfield House',
  '15 West Street',
  'Chester-le-Street',
  'Durham',
  'DH6 7AB',
  '2011-03-03',
  '0798123678',
  4,
  2,
  4,
  'Red',
  'Freda',
  '0191456789'
);

INSERT INTO diagnoses (patient_id, condition_id) VALUES (5, 7);
INSERT INTO diagnoses (patient_id, condition_id) VALUES (5, 8);
INSERT INTO prescribed_medicines (patient_id, medicine_id) VALUES (5, 3);
INSERT INTO prescribed_medicines (patient_id, medicine_id) VALUES (5, 6);