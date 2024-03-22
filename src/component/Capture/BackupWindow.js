import { useState, useEffect } from 'react';
import 'bootstrap/dist/css/bootstrap.css';
import '../../sass/Capture/BackUpWindow.scss';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCircleXmark, faHardDrive } from '@fortawesome/free-regular-svg-icons'
import {
    faDatabase, faServer, faTerminal, faDiagramNext
} from '@fortawesome/free-solid-svg-icons'
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { IP } from '../Layout/constaints';
import CustomDateTimePicker from '../action/CustomDateTimePicker.js';
import TextField from '@mui/material/TextField';
import Autocomplete from '@mui/material/Autocomplete';
const BackupWindow = ({ handleCloseWindow, data, displayBackupData }) => {
    const [isOpen, openCloseAddWindow] = useState(true)
    const [isSetServer, setServer] = useState(
        {
            "ipServer": "192.168.100.2",
            "serverName": "Database server 001",
            "type": "SSD",
            "description": "",
            "dbAccount": "",
            "dbPass": "",
            "sshAccount": "",
            "sshPass": "",
            "nasId": null,
            "nasName": ""
        }
    )
    const [isSetDb, setDb] = useState({})
    const [isSetNas,setNas] = useState({})
    const [schedule, setSchedule] = useState("");

    const [displaySchedule, setDisplaySchedule] = useState(false)

    const [dbServerList, setDbServerList] = useState([])

    const [serverRestore, setServerRestore] = useState()


    useEffect(() => {
        fetch(IP + "/api/v1/database/servers?key=")
            .then(response => response.json())
            .then(data => {
                let tmpList = []
                data.map(dt => {
                    tmpList.push({
                        "label": `${dt.serverName} ( ${dt.ipServer} )`,
                        "value": dt.id
                    })
                })
                setDbServerList(tmpList)
            })
            .catch(err => console.log(err))

    }, [])
    useEffect(() => {
        const id = displayBackupData.idServer
        if (id !== null) {
            fetch(IP + "/api/v1/database/server?id=" + id)
                .then(response => response.json())
                .then(data => setServer(data))
                .catch(err => console.log(err))
        }
    }, [])
    useEffect(() => {
        const id = displayBackupData.nasId
        if (id !== null) {
            fetch(IP + "/api/v1/nas?id=" + id)
                .then(response => response.json())
                .then(data => {
                    setNas(data)
                })
                .catch(err => console.log(err))
        }
    }, [])
    useEffect(()=>{
        const name = displayBackupData.dbName
        console.log(name)
        if (name !== null) {
            fetch(IP + `/api/v1/info/volumes?name=${name}&ip=&type=`)
                .then(response => response.json())
                .then(data => {
                    if(data.length!==0) setDb(data[0])
                })
                .catch(err => console.log(err))
        }
    },[])

    //Hàm hiển thị thông báo sau khi thêm
    const notify = (message, status) => {
        if (status === 1) {
            toast.success(message, {
                position: "top-center",
                autoClose: 4000,
                hideProgressBar: false,
                closeOnClick: true,
                draggable: true,
                progress: undefined,
                theme: "colored",
            })
        }
        else if (status === 0) {
            toast.error(message, {
                position: "top-center",
                autoClose: 4000,
                hideProgressBar: false,
                closeOnClick: true,
                draggable: true,
                progress: undefined,
                theme: "colored",
            })
        }
        else {
            toast.warn(message, {
                position: "top-center",
                autoClose: 4000,
                hideProgressBar: false,
                closeOnClick: true,
                draggable: true,
                progress: undefined,
                theme: "colored",
            })
        }

    }
    const getInfoToBackUp = () => {
        let doRestore = document.getElementById('doRestore').checked
        let doDeleteAfterBackup = document.getElementById('doDeleteAfterBackup').checked
        // let db
        return {
            "doRestore": doRestore,
            "doDeleteAfterBackup": doDeleteAfterBackup,
            "schedule": schedule,
            "idServerRestore": doRestore === true && serverRestore != null ? serverRestore.value : null
        }

    }
    const backUpDb = (data) => {
        let command = getInfoToBackUp();
        let restore = false
        let dlt = command.doDeleteAfterBackup === true ? 1 : 0
        if (command.doRestore === true && command.schedule === "") restore = true;
        if (command.idServerRestore === null && restore === true) {
            notify("Please choose server to restore", 2)
            return;
        }
        let options = {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                "scheduleRestore": command.schedule === "" ? null : command.schedule,
                "idServerRestore": command.idServerRestore,
                "ipServer": dlt === 1 ? data.ipDbLevel1 : null
            })
        }
        fetch(IP + "/api/v1/capture/backup?idServer=" + data.idServer + "&databaseName=" + data.dbName + "&idInfo=" + data.idInfo + "&restore=" + restore + "&delete=" + dlt, options)
        handleCloseWindow()
    }
    return (
        <div>
            {isOpen && (<div className='backUpWindowScreen'>
                <div className="backUpWindow">
                    <div className='btn-container d-flex justify-content-end'>
                        <button className='close-btn d-flex align-items-center' onClick={handleCloseWindow}>
                            <FontAwesomeIcon icon={faCircleXmark} />
                        </button>
                    </div>
                    {/* DB info & DB Server*/}
                    <div className="field d-flex justify-content-between">
                        <div className='input_container'>
                            <div className='input_container-icon d-flex align-items-center'>
                                <FontAwesomeIcon icon={faDatabase} />
                                <div className='input_container-icon-text'>DATABASE INFORMATION</div>
                            </div>
                            <div className='input_container-input'>
                                <div className='info_container'>
                                    <div className='info d-flex'>
                                        <div className='info-title'>DATABASE NAME : </div>
                                        <div className='info-data'>{isSetDb.databaseName}</div>
                                    </div>
                                    <div className='info d-flex'>
                                        <div className='info-title'>DATABASE IP : </div>
                                        <div className='info-data'>{isSetDb.ipDb}</div>
                                    </div>
                                    <div className='info d-flex'>
                                        <div className='info-title'>TOTAL VOLUME : </div>
                                        <div className='info-data'>{isSetDb.volumeTotal}</div>
                                    </div>
                                    <div className='info d-flex'>
                                        <div className='info-title'>USED VOLUME : </div>
                                        <div className='info-data'>{isSetDb.volumeUsed}</div>
                                    </div>
                                    <div className='info d-flex'>
                                        <div className='info-title'>FREE VOLUME : </div>
                                        <div className='info-data'>{isSetDb.volumeFree}</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div className='input_container'>
                            <div className='input_container-icon d-flex align-items-center'>
                                <FontAwesomeIcon icon={faServer} />
                                <div className='input_container-icon-text'>DATABASE SERVER</div>
                            </div>
                            <div className='input_container-input'>
                                <div className='info_container'>
                                    <div className='info d-flex'>
                                        <div className='info-title'>SERVER NAME : </div>
                                        <div className='info-data'>{isSetServer.serverName}</div>
                                    </div>
                                    <div className='info d-flex'>
                                        <div className='info-title'>SERVER IP : </div>
                                        <div className='info-data'>{isSetServer.ipServer}</div>
                                    </div>
                                    <div className='info d-flex'>
                                        <div className='info-title'>SERVER TYPE : </div>
                                        <div className='info-data'>{isSetServer.type}</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    {/* Server IP & DB Account */}
                    <div className="field d-flex justify-content-between">
                        <div className='input_container'>
                            <div className='input_container-icon d-flex align-items-center'>
                                <FontAwesomeIcon icon={faHardDrive} />
                                <div className='input_container-icon-text'>BACKUP NAS</div>
                            </div>
                            <div className='input_container-input'>
                                <div className='info_container'>
                                    <div className='info d-flex'>
                                        <div className='info-title'>NAS NAME : </div>
                                        <div className='info-data'>{isSetNas.nasName}</div>
                                    </div>
                                    <div className='info d-flex'>
                                        <div className='info-title'>NAS IP : </div>
                                        <div className='info-data'>{isSetNas.ip}</div>
                                    </div>
                                    <div className='info d-flex'>
                                        <div className='info-title'>PORT : </div>
                                        <div className='info-data'>{isSetNas.port}</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div className='input_container'>
                            <div className='input_container-icon d-flex align-items-center'>
                                <FontAwesomeIcon icon={faTerminal} />
                                <div className='input_container-icon-text'>ADDITION COMMAND</div>
                            </div>
                            <div className='input_container-input'>
                                <div className='info_container'>
                                    <div className='info d-flex'>
                                        <div className='optional d-flex align-items-center'
                                            style={{ paddingTop: '8px', paddingRight: "8px" }}>
                                            <input type='checkbox' id='doRestore' onChange={(e) => {
                                                setDisplaySchedule(e.target.checked)
                                            }} />
                                        </div>
                                        <div className='info-title d-flex align-items-center'
                                            style={{ paddingTop: '8px' }}
                                        >RESTORE </div>
                                        {displaySchedule && <div className='info-data'>
                                            <CustomDateTimePicker schedule={schedule} setSchedule={setSchedule} />
                                        </div>}
                                    </div>
                                    {displaySchedule && <div className='info d-flex'>
                                        <Autocomplete
                                            disablePortal
                                            id="combo-box-demo"
                                            options={dbServerList}
                                            onChange={(event, value) => setServerRestore(value)}
                                            isOptionEqualToValue={(option, value) => option.id === value.id}
                                            sx={{
                                                width: '100%', paddingTop: "8px",
                                                "& .MuiSvgIcon-root": {
                                                    color: "white"
                                                },
                                                "& .MuiFormLabel-root": {
                                                    color: "white",
                                                    fontSize: "1em"
                                                },
                                                "& .MuiInputBase-input": {
                                                    color: "white",
                                                    fontSize: "0.9em"
                                                },
                                                "& fieldset": {
                                                    borderColor: "white"
                                                }

                                            }}
                                            renderInput={(params) => <TextField {...params} label="Choose restore database server" />}
                                        />
                                    </div>}
                                    <div className='info d-flex'>
                                        <div className='optional d-flex align-items-center'
                                            style={{ paddingTop: '8px', paddingRight: "8px" }}>
                                            <input type='checkbox' id='doDeleteAfterBackup' />
                                        </div>
                                        <div className='info-title d-flex align-items-center'
                                            style={{ paddingTop: '8px' }}
                                        >DELETE AFTER BACKUP </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    {/* Button */}
                    <div className='btn-container d-flex justify-content-end'>
                        <button className='btn d-flex align-items-center'
                            onClick={() => backUpDb(data)}
                            style={{ backgroundColor: '#699BF7', color: "white" }}
                        >
                            <div className='btn-icon d-flex align-items-center' >
                                <FontAwesomeIcon icon={faDiagramNext} />
                            </div>
                            <div className='btn-text'  >Back up</div>
                        </button>
                    </div>
                </div>
            </div>)
            }

        </div >
    )
}
export default BackupWindow;