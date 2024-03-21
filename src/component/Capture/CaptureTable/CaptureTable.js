import React, { useEffect, useState } from 'react'
import { Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TablePagination, Tooltip } from '@mui/material'
import '../../../sass/Capture/CaptureTable/CaptureTable.scss'
import { faCircleExclamation, faDiagramNext, faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons';
import { faWindowRestore } from '@fortawesome/free-regular-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import DropDownInput from '../../action/DropDownInput';
import BackupWindow from '../BackupWindow';
import RestoreWindow from '../RestoreWindow';
import { IP } from '../../Layout/constaints';
import SimpleDialogDemo from '../../action/SimpleDialog';
import LinearWithValueLabel from '../../action/LinearProgressWithLabel';
import BackUpProgress from '../../action/BackUpProgress';
function CaptureTable() {
  var timer = 1000
  const [captureList, setCaptrueList] = useState([])
  const [displayCaptureList, setDisplayCaptrueList] = useState([])
  const [isOpenBackupWindow, openCloseBackupWindow] = useState(false);
  const [isOpenRestoreWindow, openCloseRestoreWindow] = useState(false);
  const [page, setPage] = useState(0)
  const [rowsPerPage, setRowsPerPage] = useState(10)
  const [dbServerList, setDbServerList] = useState([])
  const [selectedServer, setSelectedServer] = useState("")
  const [selectedProvince, setSelectedProvince] = useState("")
  const [province, setProvince] = useState([
    {
      label: "HNI",
      value: "HNI"
    },
    {
      label: "DLK",
      value: "DLK"
    },
    {
      label: "HCM",
      value: "HCM"
    }
  ])
  const [backupData, setBackupData] = useState({});
  const [restoreData, setRestoreData] = useState({});
  useEffect(() => {
    fetch(IP + "/api/v1/captures")
      .then(response => response.json())
      .then(data => {
        setCaptrueList(data)
        setDisplayCaptrueList(data)
        // if (sessionStorage.getItem("restoreInfo") === null) {
        //   let restoreInfo = []
        //   data.forEach(item => {
        //     if (item.dbName !== null && item.ipDbRunning !== null) {
        //       restoreInfo.push({
        //         'capture_id': item.dbName.concat(item.ipDbRunning),
        //         'dbName': item.dbName,
        //         'idRestore': null,
        //         "isBackuping": 0,
        //         "restoreAfterBackup": 0
        //       })
        //     }
        //   })
        //   sessionStorage.setItem("restoreInfo", JSON.stringify(restoreInfo))
        // }
      })
      .catch(err => console.log(err))
    const interval = setInterval(() => {
      fetch(IP + "/api/v1/captures")
        .then(response => response.json())
        .then(data => {
          const check = data.find(item => item.backupStatus.includes("Processing") || item.statusRestore.includes("Processing"))
          // if(check!==undefined) timer= 5000;
          // else timer =1000
          setCaptrueList(data)
          getCapTureListByCondition(data)
        })
        .catch(err => console.log(err))
    }, timer);
    return () => {
      sessionStorage.removeItem("condition")
      clearInterval(interval);
    }
  }, []);
  useEffect(() => {
    fetch(IP + "/api/v1/database/servers?key=")
      .then(response => response.json())
      .then(data => {
        let listServer = data.map(server => {
          return {
            'label': server.serverName,
            'value': server.id
          }
        })
        setDbServerList(listServer)
      })
      .catch(err => console.log(err))
  }, [])
  const setCondition = () => {
    let condition = {
      "probeName": document.getElementById('content').value === "" ? null : document.getElementById('content').value,
      "dbServer": selectedServer === "" ? null : selectedServer,
      "province": selectedProvince === "" ? null : selectedProvince,
      "monitorStatus": document.getElementById('monitorStatus').value === "" ? null : document.getElementById('monitorStatus').value,
      "backupStatus": document.getElementById('backupStatus').value === "" ? null : document.getElementById('backupStatus').value
    }
    sessionStorage.setItem("condition", JSON.stringify(condition))
    getCapTureListByCondition(captureList)
  }
  const getCapTureListByCondition = (data) => {
    let condition = JSON.parse(sessionStorage.getItem("condition"))
    if (condition !== null) {
      let displayCaptureList = data.filter((item) => {
        return (condition.probeName === null || item.probeName.toLowerCase().includes(condition.probeName.toLowerCase())) &&
          (!condition.dbServer || item.idServer === condition.dbServer) &&
          (!condition.province || item.province === condition.province) &&
          (!condition.monitorStatus || item.statusMonitor === condition.monitorStatus || condition.monitorStatus === "") &&
          (!condition.backupStatus || (item.backupStatus !== null && item.backupStatus.toLowerCase().includes(condition.backupStatus.toLowerCase()))); // Sửa ở đây
      });
      setDisplayCaptrueList(displayCaptureList)
    }
    else {
      setDisplayCaptrueList(data)
    }
  }
  const getBackupData = (idServer, dbName, idInfo, ipDbLevel1, ipDbRunning) => {
    return {
      "idServer": idServer,
      "dbName": dbName,
      "idInfo": idInfo,
      "ipDbLevel1": ipDbLevel1,
      "id": dbName.concat(ipDbRunning),
    }
  }

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  }
  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value), 10)
    setPage(0)
  }
  // Hàm đóng mở cửa sổ backup
  const handleOpenBackupWindow = (data) => {
    openCloseBackupWindow(true)
    setBackupData(data)

  }
  const handleCloseBackupWindow = () => {
    openCloseBackupWindow(false)
  }
  // Ham dong mo cua so restore
  const handleOpenRestoreWindow = (data) => {
    openCloseRestoreWindow(true)
    setRestoreData(data)

  }
  const handleCloseRestoreWindow = () => {
    openCloseRestoreWindow(false)
  }
  //Ham set color for ipdb
  const setColorForIpDb = (text, status) => {
    if (status !== null) return "red"
    if (text != null) {
      let check = text.includes("Deleted")
      if (check === false) return "#00FF1A"
    }

    return "#FFF61C"
  }

  const setiIdRestore = (dbName, ipDbRunning) => {
    if (dbName !== null && ipDbRunning !== null) {
      const id = dbName.concat(ipDbRunning)
      if ((sessionStorage.getItem("restoreInfo") !== null)) {
        let restoreInfo = sessionStorage.getItem("restoreInfo")
        restoreInfo = JSON.parse(restoreInfo)
        let ob = restoreInfo.find(item => item.capture_id === id)
        return ob.idRestore
      }
    }
    return null;
  }
  // const checkBackuping = (dbName, ipDbRunning) => {
  //   if (dbName !== null && ipDbRunning !== null) {
  //     const id = dbName.concat(ipDbRunning)
  //     if ((sessionStorage.getItem("restoreInfo") !== null)) {
  //       let restoreInfo = sessionStorage.getItem("restoreInfo")
  //       restoreInfo = JSON.parse(restoreInfo)
  //       let ob = restoreInfo.find(item => item.capture_id === id && item.isBackuping === 1)
  //       if (ob !== undefined) {
  //         console.log("This is ob: ",ob)
  //         return ob.isBackuping;
  //       }

  //     }
  //   }
  //   return 0;
  // }
  const renderBackupStatus = (data) => {
    if (data.backupStatus.includes("Processing")) {
      return (<BackUpProgress
        processId={
          {
            // "databaseName": data.dbName,
            // "idRestore": setiIdRestore(data.dbName, data.ipDbRunning),
            // "ipDbRunning": data.ipDbRunning,
            // 'isRestoreAfterBackup': checkBackuping(data.dbName, data.ipDbRunning),
            "backupStatus":data.processBackup
          }
        }
      />)
    }
    return data.backupStatus;
  }
  const renderRestoreStatus = (data) => {
    if (data.statusRestore.includes("Processing")) {
      return (<LinearWithValueLabel
        processId={
          {
            "restoreProcess":data.processRestore,
            // "databaseName": data.dbName,
            // "idRestore": setiIdRestore(data.dbName, data.ipDbRunning),
            // "ipDbRunning": data.ipDbRunning
          }
        }
      />)
    }
    return data.statusRestore;
  }
  return (
    <div className='CaptureTable'>
      <div className='searchBar d-flex justify-content-between align-items-end'>
        <div className='searchTitle'>
          <div className="conditionTitle">Probe name</div>
          <input type='text' placeholder="Search by probe name..." id="content"></input>
        </div>
        <div className="searchProbe SearchServer">
          <div className="conditionTitle">Database server</div>
          <DropDownInput defaultContent="Search database server" inputOptions={dbServerList} handleSelect={setSelectedServer} ></DropDownInput>
        </div>
        <div className="searchProbe">
          <div className="conditionTitle">Province</div>
          <DropDownInput id="provice" defaultContent="Search province" inputOptions={province} handleSelect={setSelectedProvince} ></DropDownInput>
        </div>
        <div className='chooseTimeRange'>
          <div className="conditionTitle">Monitor Status</div>
          <select id='monitorStatus'>
            <option value="">All</option>
            <option value="Monitor">Monitoring</option>
            <option value="No monitor">No monitoring</option>
          </select>
        </div>
        <div className='chooseTimeRange'>
          <div className="conditionTitle">Backup Status</div>
          <select id='backupStatus'>
            <option value="">All</option>
            <option value="Finished">Finished</option>
            <option value="Processing">Processing </option>
            <option value="error">Error</option>
            <option value="NA">N/A</option>
          </select>
        </div>
        <button className='searchButton d-flex'
          onClick={() => { setCondition() }}
        >
          <div className='searchButton-icon'>
            <FontAwesomeIcon icon={faMagnifyingGlass} rotation={90}></FontAwesomeIcon>
          </div>
          <div className='searchButton-text'>Search</div>
        </button>
      </div>
      <TableContainer component={Paper} className='CaptureTableContainer' style={{ maxWidth: '100%', width: '100%', overflowX: 'auto' }}>
        <Table stickyHeader aria-label=" table" sx={{
          '& .MuiTableCell-sizeMedium': {
            padding: '5px 10px',
          },
        }} >
          <TableHead>
            <TableRow>
              <TableCell width="4%" >No</TableCell>
              <TableCell width="11%" >Probe name</TableCell>
              <TableCell width="8%" >DB name</TableCell>
              <TableCell width="6%">Province</TableCell>
              <TableCell width="9%" >IP DB Level 1</TableCell>
              <TableCell width="9%">IP DB Level 2</TableCell>
              <TableCell width="9%">IP DB Running</TableCell>
              <TableCell width="8%">Monitor Status</TableCell>
              <TableCell width="6%">Volume</TableCell>
              <TableCell width="7%">Start time</TableCell>
              <TableCell width="7%">Stop time</TableCell>
              <TableCell width="14%">Backup Status</TableCell>
              <TableCell width="14%">Restore Status</TableCell>
              <TableCell width="10%">Action</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>

            {
              displayCaptureList.length !== 0 ?
                (displayCaptureList.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage).map((data, index) => {
                  return (
                    <TableRow key={index + 1}>
                      <TableCell>
                        <div className='d-flex justify-content-around align-items-center'>
                          {data.status_connect !== null &&
                            <Tooltip title={data.message}>
                              <FontAwesomeIcon icon={faCircleExclamation} beatFade style={{ color: "#e91607", }} size='xs' />
                            </Tooltip>
                          }
                          <span className='d-flex justify-content-around align-items-center'>
                            {index + 1}
                          </span>
                        </div>
                      </TableCell>
                      <TableCell>
                        <Tooltip title={<div>{data.probeName}</div>}>
                          <div>{data.probeName}</div>
                        </Tooltip>
                      </TableCell>
                      <TableCell>
                        <Tooltip title={<div>{data.dbName}</div>}>
                          <div>{data.dbName}</div>
                        </Tooltip>
                      </TableCell>
                      <TableCell>{data.province}</TableCell>
                      <TableCell>
                        <Tooltip title={<div>{data.ipDbLevel1}</div>}>
                          <div style={{ color: setColorForIpDb(data.ipDbLevel1, data.status_connect) }} >{data.ipDbLevel1}</div>
                        </Tooltip>
                      </TableCell>
                      <TableCell>
                        <Tooltip title={<div>{data.ipDbLevel2}</div>}>
                          <div style={{ color: setColorForIpDb(data.ipDbLevel2, data.status_connect) }} >{data.ipDbLevel2}</div>
                        </Tooltip>
                      </TableCell>
                      <TableCell>{data.ipDbRunning}</TableCell>
                      <TableCell>{data.statusMonitor}</TableCell>
                      <TableCell>{data.totalVolume}</TableCell>
                      <TableCell>{data.startTime}</TableCell>
                      <TableCell>{data.stopTime}</TableCell>
                      <TableCell>
                        {renderBackupStatus(data)}
                      </TableCell>
                      <TableCell>
                        {renderRestoreStatus(data)}
                      </TableCell>
                      <TableCell>
                        <div className='d-flex justify-content-evenly'>
                          <div className='action'>
                            <Tooltip title={data.status_connect !== null ? "Lost connection" : setColorForIpDb(data.ipDbLevel1) === "#FFF61C" ? "Deleted" : "Backup"}>
                              <button
                                disabled={data.status_connect !== null || setColorForIpDb(data.ipDbLevel1) === "#FFF61C"}
                                onClick={() => { handleOpenBackupWindow(getBackupData(data.idServer, data.dbName, data.id_info_capture_setting, data.ipDbLevel1, data.ipDbRunning)) }}
                              >
                                <FontAwesomeIcon icon={faDiagramNext} style={{ color: data.status_connect !== null || setColorForIpDb(data.ipDbLevel1) === "#FFF61C" ? "gray" : "#699BF7" }} />
                              </button>
                            </Tooltip>
                          </div>
                          <div className='action'>
                            <Tooltip title={data.status_connect !== null ? "Lost connection" : "Restore"}>
                              <button
                                disabled={data.status_connect !== null}
                                onClick={() => { handleOpenRestoreWindow(getBackupData(data.idServer, data.dbName, data.id_info_capture_setting, null, data.ipDbRunning)) }}
                              >
                                <FontAwesomeIcon icon={faWindowRestore} style={{ color: data.status_connect !== null ? "gray" : "#FFD233" }} />
                              </button>
                            </Tooltip>
                          </div>
                          <div className='action'>
                            <SimpleDialogDemo ipDbLevel1={data.ipDbLevel1} ipDbLevel2={data.ipDbLevel2}
                              databaseName={data.dbName} idInfo={data.id_info_capture_setting}
                            />
                          </div>
                        </div>

                      </TableCell>
                    </TableRow>
                  )
                })) : (
                  <TableRow style={{ border: "none", height: "100%" }}>

                  </TableRow>
                )}
          </TableBody>
        </Table>
      </TableContainer>
      <TablePagination
        component="div"
        count={captureList.length}
        page={page}
        onPageChange={handleChangePage}
        rowsPerPageOptions={[5, 10]}
        rowsPerPage={rowsPerPage}
        onRowsPerPageChange={handleChangeRowsPerPage}
        sx={{
          color: "white",
          '& p': {
            margin: 0
          }
        }}
      />
      {isOpenBackupWindow && <BackupWindow handleCloseWindow={handleCloseBackupWindow} data={backupData} />}
      {isOpenRestoreWindow && <RestoreWindow handleCloseWindow={handleCloseRestoreWindow} data={restoreData} />}
    </div>
  )
}

export default CaptureTable