#!/bin/bash
# Crea el esquema de la auditoría central en FREEPDB1 si todavía no existe.
# La imagen de Oracle ejecuta los scripts de /opt/oracle/scripts/startup en cada arranque,
# por eso el script es idempotente: si el usuario ya existe, no hace nada.
# Las credenciales llegan del archivo .env (ORACLE_AUDIT_USERNAME y ORACLE_AUDIT_PASSWORD).

sqlplus -s / as sysdba <<SQL
WHENEVER SQLERROR EXIT FAILURE
SET SERVEROUTPUT ON
ALTER SESSION SET CONTAINER = FREEPDB1;

DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count FROM dba_users WHERE username = UPPER('${ORACLE_AUDIT_USERNAME}');
    IF v_count > 0 THEN
        DBMS_OUTPUT.PUT_LINE('El usuario de auditoría ${ORACLE_AUDIT_USERNAME} ya existe');
        RETURN;
    END IF;

    SELECT COUNT(*) INTO v_count FROM dba_tablespaces WHERE tablespace_name = 'SUPPORT_AUDIT_DATA';
    IF v_count = 0 THEN
        EXECUTE IMMEDIATE q'[CREATE TABLESPACE support_audit_data
            DATAFILE '/opt/oracle/oradata/FREE/FREEPDB1/support_audit_data01.dbf'
            SIZE 100M AUTOEXTEND ON NEXT 50M MAXSIZE 1G]';
    END IF;

    EXECUTE IMMEDIATE 'CREATE USER ${ORACLE_AUDIT_USERNAME} IDENTIFIED BY "${ORACLE_AUDIT_PASSWORD}"'
        || ' DEFAULT TABLESPACE support_audit_data QUOTA 1G ON support_audit_data';
    EXECUTE IMMEDIATE 'GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE TO ${ORACLE_AUDIT_USERNAME}';
    DBMS_OUTPUT.PUT_LINE('Usuario de auditoría ${ORACLE_AUDIT_USERNAME} creado en FREEPDB1');
END;
/
EXIT
SQL
