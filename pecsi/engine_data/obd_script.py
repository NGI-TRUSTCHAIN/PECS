#!/system/bin/python3

from time import sleep
import obd
from obd import OBD, scan_serial, OBDStatus
import logging as log
import json
from signal import signal, SIGINT
import shutil as sh

MAS_OBD_QUERIES = {
    'rpm': obd.commands.RPM,
    'vehicle-speed': obd.commands.SPEED,
    'throttle-position': obd.commands.THROTTLE_POS,
    'o2-sensors': obd.commands.O2_B1S2
}

logger = log.getLogger(__name__)

def plog(s: str) -> None:
    print(s)
    logger.info(s)

def sigintHandler(signum, frame, conn: OBD) -> None:
    plog('Handling SIGINT...')
    conn.close()
    plog("Connection closed\nExiting program...")
    exit(0)

def saveJson(conn: OBD, outfile) -> None:
    engineData = {}
    for key, value in MAS_OBD_QUERIES.items():
        res = conn.query(value)
        res = res.value.magnitude # return numeric value
        engineData[key] = res

    with open(outfile, 'w') as f:
        json.dump({'engineData': engineData}, f, indent=3)


def main() -> None:
    log.basicConfig(filename='obd.log', level=log.INFO)

    signal(SIGINT, sigintHandler)

    conn = OBD() # auto search

    if conn.status() == OBDStatus.NOT_CONNECTED: plog("ELM327 not connected")
    if conn.status() == OBDStatus.ELM_CONNECTED: plog("Connected with ELM327")
    if conn.status() == OBDStatus.OBD_CONNECTED: plog("Connected to car but ignition is off")
    if conn.status() == OBDStatus.CAR_CONNECTED: plog("Connected to ECU")

    if conn.port_name() == "": plog("No port found")
    else: plog(f"Port found: {conn.port_name()}")

    plog(f"Using protocol '{conn.protocol_name()}'")

    while True:
        saveJson(conn, '/storage/engineData.json')
        sleep(1.5)


main() if __name__ == '__main__' else None

