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
function CaptureTable() {
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
      })
      .catch(err => console.log(err))
  }, [])
  useEffect(() => {
    const interval = setInterval(() => {
      fetch(IP + "/api/v1/captures")
        .then(response => response.json())
        .then(data => {
          setCaptrueList(data)
        })
        .catch(err => console.log(err))
      getCapTureListByCondition()
    }, 5000);
    return () => {
      sessionStorage.clear()
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
    getCapTureListByCondition()
  }
  const getCapTureListByCondition = () => {
    let condition = JSON.parse(sessionStorage.getItem("condition"))
    if (condition !== null) {
      let displayCaptureList = captureList.filter((item) => {
        return (!condition.probeName || item.probeName.toLowerCase().includes(condition.probeName.toLowerCase())) &&
          (!condition.dbServer || item.idServer === condition.dbServer) &&
          (!condition.province || item.province === condition.province) &&
          (!condition.monitorStatus || item.monitorStatus === condition.monitorStatus) &&
          (!condition.backupStatus || (item.backupStatus !== null && item.backupStatus.toLowerCase().includes(condition.backupStatus.toLowerCase()))); // Sửa ở đây
      });
      console.log(displayCaptureList)
      setDisplayCaptrueList(displayCaptureList)
    }
    // else {
    //   fetch(IP + "/api/v1/captures?monitorStatus=2" + "&province=" + "&backupStatus=" + "&probeName=")
    //     .then(response => response.json())
    //     .then(data => setCaptrueList(data))
    //     .catch(err => console.log(err))
    // }
    console.log(condition)

  }
  const getBackupData = (idServer, dbName, idInfo, ipDbLevel1) => {
    return {
      "idServer": idServer,
      "dbName": dbName,
      "idInfo": idInfo,
      "ipDbLevel1": ipDbLevel1
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
  const setColorForIpDb = (text) => {
    if (text != null) {
      let check = text.includes("Deleted")
      if (check === false) return "#00FF1A"
    }
    return "#FFF61C"
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
              <TableCell width="3%" >No</TableCell>
              <TableCell width="11%" >Probe name</TableCell>
              <TableCell width="8%" >DB name</TableCell>
              <TableCell width="5%">Province</TableCell>
              <TableCell width="9%" >IP DB Level 1</TableCell>
              <TableCell width="9%">IP DB Level 2</TableCell>
              <TableCell width="9%">IP DB Running</TableCell>
              <TableCell width="8%">Monitor Status</TableCell>
              <TableCell width="6%">Volume (GB)</TableCell>
              <TableCell width="7%">Start time</TableCell>
              <TableCell width="7%">Stop time</TableCell>
              <TableCell width="14%">Backup Status</TableCell>
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
                            <Tooltip title="The connection to clickhouse server is lost">
                              <FontAwesomeIcon icon={faCircleExclamation} beatFade style={{ color: "#e91607", }} />
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
                          <div style={{ color: setColorForIpDb(data.ipDbLevel1) }} >{data.ipDbLevel1}</div>
                        </Tooltip>
                      </TableCell>
                      <TableCell>
                        <Tooltip title={<div>{data.ipDbLevel2}</div>}>
                          <div style={{ color: setColorForIpDb(data.ipDbLevel2) }} >{data.ipDbLevel2}</div>
                        </Tooltip>
                      </TableCell>
                      <TableCell>{data.ipDbRunning}</TableCell>
                      <TableCell>{data.statusMonitor}</TableCell>
                      <TableCell>{data.totalVolume}</TableCell>
                      <TableCell>{data.startTime}</TableCell>
                      <TableCell>{data.stopTime}</TableCell>
                      <TableCell>
                        <Tooltip title={<div>{data.backupStatus}</div>}>
                          <div>{data.backupStatus}</div>
                        </Tooltip>
                      </TableCell>
                      <TableCell>
                        <div className='d-flex justify-content-evenly'>
                          <div className='action'>
                            <button
                              onClick={() => { handleOpenBackupWindow(getBackupData(data.idServer, data.dbName, data.id_info_capture_setting, data.ipDbLevel1)) }}
                            >
                              <FontAwesomeIcon icon={faDiagramNext} style={{ color: "#699BF7" }} />
                            </button>
                          </div>
                          <div className='action'>
                            <button
                              onClick={() => { handleOpenRestoreWindow(getBackupData(data.idServer, data.dbName, data.id_info_capture_setting)) }}
                            >
                              <FontAwesomeIcon icon={faWindowRestore} style={{ color: "#FFD233" }} />
                            </button>
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