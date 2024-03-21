import React,{ useState, useEffect } from 'react';
import 'bootstrap/dist/css/bootstrap.css';
import '../../sass/Capture/BackUpWindow.scss';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCircleXmark,faWindowRestore } from '@fortawesome/free-regular-svg-icons'
import {
    faDatabase, faSpinner
} from '@fortawesome/free-solid-svg-icons'
import {  toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { IP } from '../Layout/constaints';
import TextField from '@mui/material/TextField';
import Autocomplete from '@mui/material/Autocomplete';
import { Box } from '@mui/material';
import LinearWithValueLabel from '../action/LinearProgressWithLabel';
import { json } from 'react-router-dom';

const RestoreWindow = ({ handleCloseWindow, id, data }) => {
    const [isOpen, openCloseAddWindow] = useState(true)
    const [isRestoring, setRestoring] = useState(false)
    const [serverList, setServerList] = useState([])
    const [restoreDatabaseList, setRestoreDatabaseList] = useState([])
    const [choosenServer, setChoosenServer] = useState();
    const [choosenDb, setChoosenDb] = useState()


    useEffect(() => {
        fetch(IP + "/api/v1/info/database/restore?databaseName=" + data.dbName)
            .then(response => response.json())
            .then(data => {
                let tmpList = []
                data.map(dt => {
                    tmpList.push({
                        "label": dt.databaseName,
                        "value": dt.id
                    })
                })
                setRestoreDatabaseList(tmpList)
            })
            .catch(err => console.log(err))

    }, [])
    useEffect(() => {
        fetch(IP + "/api/v1/database/servers?key=")
            .then(response => response.json())
            .then(data => {
                let tmpList = []
                data.map(dt => {
                    tmpList.push({
                        "label": dt.serverName,
                        "value": dt.id
                    })
                })
                setServerList(tmpList)
            })
            .catch(err => console.log(err))

    }, [])



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

    const restoreDb = () => {
        
        if (choosenServer == null || choosenDb == null) {
            notify(2, "Please choose all the fields")
        }
        else {
            let options = {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                }
            }
            fetch(IP + "/api/v1/capture/restore?idInfoDatabase=" + choosenDb.value + "&idServer=" + choosenServer.value, options)
            let restoreInfo = sessionStorage.getItem("restoreInfo")
            if(restoreInfo!==null){
                let tmp = [...JSON.parse(restoreInfo)]
                tmp = tmp.map(item =>{
                    if(data.id === item.capture_id){
                        return {
                            ...item,
                            'idRestore': choosenDb.value
                        }
                    }
                    else return item;
                })
                sessionStorage.setItem("restoreInfo",JSON.stringify(tmp))
            }
            handleCloseWindow()
        }
    }

    return (
        <React.Fragment>
            <div>
                {isOpen && (<div className='backUpWindowScreen'>
                    <div className="backUpWindow">
                        <div className='btn-container d-flex justify-content-end'>
                            <button className='close-btn d-flex align-items-center' onClick={handleCloseWindow}>
                                <FontAwesomeIcon icon={faCircleXmark} />
                            </button>
                        </div>
                        <div className="field d-flex justify-content-between">
                            <div className='input_container'>
                                <div className='input_container-icon d-flex align-items-center'>
                                    <FontAwesomeIcon icon={faDatabase} />
                                    <div className='input_container-icon-text'>DATABASE INFORMATION</div>
                                </div>
                            </div>
                        </div>
                        <Autocomplete
                            disablePortal
                            id="combo-box-demo"
                            options={serverList}
                            onChange={(event, value) => setChoosenServer(value)}
                            isOptionEqualToValue={(option, value) => option.id === value.id}
                            sx={{
                                width: '100%', color: "white", padding: "1.5em 0",
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
                                    borderColor: "#FF1CE8"
                                },
                                "& label.Mui-focused": {
                                    color: "#FF1CE8",
                                    fontSize: "1.2em"
                                },
                                "& .MuiOutlinedInput-root": {
                                    "&.Mui-focused fieldset": {
                                        borderColor: "#FF1CE8"
                                    }
                                }

                            }}
                            renderInput={(params) => <TextField {...params} label="Choose backup server" />}
                        />
                        <Autocomplete
                            disablePortal
                            id="combo-box-demo"
                            options={restoreDatabaseList}
                            onChange={(event, value) => setChoosenDb(value)}
                            isOptionEqualToValue={(option, value) => option.id === value.id}
                            sx={{
                                width: '100%', color: "white", padding: "1.5em 0",
                                "& .MuiFormLabel-root": {
                                    color: "white",
                                    fontSize: "1em"
                                },
                                "& .MuiInputBase-input": {
                                    color: "white",
                                    fontSize: "1em"
                                },
                                "& fieldset": {
                                    borderColor: "#FF1CE8"
                                },
                                "& label.Mui-focused": {
                                    color: "#FF1CE8",
                                    fontSize: "1.2em"
                                },
                                "& .MuiOutlinedInput-root": {
                                    "&.Mui-focused fieldset": {
                                        borderColor: "#FF1CE8"
                                    }
                                }

                            }}
                            renderInput={(params) => <TextField {...params} label="Choose restore database" />}
                        />
                        {/* Button */}
                        <div className='btn-container d-flex justify-content-end'>
                            <button className='btn d-flex align-items-center'
                                onClick={() => restoreDb()}
                                style={{ backgroundColor: '#699BF7', color: "white" }}
                            >
                                <div className='btn-icon d-flex align-items-center' >
                                    <FontAwesomeIcon icon={faWindowRestore} />
                                </div>
                                <div className='btn-text'  >Restore</div>
                            </button>
                        </div>
                    </div>
                </div>)
                }

            </div >
        </React.Fragment>
    )
}
export default RestoreWindow;