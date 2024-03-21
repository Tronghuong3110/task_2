import { useState, useEffect } from 'react';
import 'bootstrap/dist/css/bootstrap.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCircleXmark, faNoteSticky, faUser } from '@fortawesome/free-regular-svg-icons'
import {
    faFloppyDisk, faTerminal, faHardDrive, faMapPin, faInbox, faLock, faPlay, faSpinner
} from '@fortawesome/free-solid-svg-icons'
import '../../sass/Module/AddModule.scss';
import { toast } from 'react-toastify'
import { IP } from '../Layout/constaints';
import SmallConfirm from '../action/SmallConfirm';
const AddNas = ({ handleCloseWindow, id, rerender }) => {    
    const [fptPassword,setFptPassword]= useState("")
    const [isEditNas, setEditNas] = useState({
        "id": null,
        "nasName": null,
        "ip": null,
        "port": null,
        "path": null,
        "username": null,
        "password": fptPassword,
        "description": null
    })
    const [confirmScreen, setOpenConfirmScreen] = useState(false)
    const [action, setAction] = useState({
        "module": null,
        "action": "update",
        "note": "Please check all the fields carefully before updating"
    })
    const [testing,setTesting] = useState(false)
    useEffect(() => {
        if (id !== null) {
            fetch(IP + "/api/v1/nas?id=" + id)
                .then(response => response.json())
                .then(data => {
                    setEditNas(data)
                    setFptPassword(data.password)
                })
                .catch(err => console.log(err))
        }
    }, [])
    // useEffect(() => {
    //     fetch(IP + "/api/v1/typeModule")
    //         .then(response => response.json())
    //         .then(data => setTypeModule(data))
    //         .catch(err => console.log(err))
    // }, [])

    // Thêm mới một module mẫu
    const addOrEditModule = (id) => {
        let data = getNasInfo();
        console.log(id)
        if (findEmptyFields(data).length > 0) {
            console.log(findEmptyFields(data))
            let message = "Field ";
            let arr = findEmptyFields(data);
            if (arr.length === 1) message += arr[0] + " is empty"
            else {
                for (let i = 0; i < arr.length; i++) {
                    if (i !== arr.length - 1) message += arr[i] + ", "
                    else message += arr[i]
                }
                message += " are empty"
            }
            notify(message, 2)
        }
        else {
            if (id !== null) {
                data = {
                    ...data,
                    password: null
                }
            }
            let options = {
                method: id === null ? "POST" : "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(data)
            }
            if (id === null) {

                fetch(IP + "/api/v1/nas/import", options)
                    .then(response => response.json())
                    .then(data => {
                        if (data.code === 1) {
                            notify(data.message, data.code)
                            handleCloseWindow();
                            rerender()
                        }
                        else notify(data.message, data.code)
                    })
                    .catch(err => console.log(err))
            }
            else {
                setAction({
                    ...action,
                    module: [id]
                })
                setOpenConfirmScreen(true)
            }

        }
    }
    const updateNAS = (id, action) => {
        let data = getNasInfo();
        console.log(data)
        let options = {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        }
        fetch(IP + "/api/v1/nas", options)
            .then(response => response.json())
            .then(data => {
                console.log(data)
                if (data.code === 1) {
                    notify(data.message, data.code)
                    handleCloseWindow();
                    rerender()
                }
                else {

                    notify(data.message, data.code)
                }
            })
            .catch(err => console.log(err))
    }
    const getNasInfo = () => {
        return {
            "nasName": document.getElementById("nasName").value,
            "ip": document.getElementById("nasIp").value,
            "port": document.getElementById("port").value,
            "path": document.getElementById("path").value,
            "username": document.getElementById("fptUsername").value,
            "password": fptPassword,
            "description": document.getElementById("description").value,
            "id": id
        }
    }

    const testFTPConnection = () => {
        let dataTestConnection = {
            "port": document.getElementById("port").value,
            "username": document.getElementById("fptUsername").value,
            "password": fptPassword,
            "ip": document.getElementById("nasIp").value
        }
        let emptyField = findEmptyFields(dataTestConnection)
        if (emptyField.length > 0) {
            let message = "Field ";
            if (emptyField.length === 1) message += emptyField[0] + " is empty"
            else {
                for (let i = 0; i < emptyField.length; i++) {
                    if (i !== emptyField.length - 1) message += emptyField[i] + ", "
                    else message += emptyField[i]
                }
                message += " are empty"
            }
            notify(message, 2)
        }
        else {
            let options = {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                }
            }
            setTesting(true)
            fetch(IP + `/api/v1/test/connect/ftp?ip=${dataTestConnection.ip}&username=${dataTestConnection.username}&pass=${fptPassword}&port=${dataTestConnection.port}`, options)
                .then(response => response.json())
                .then(data => {
                    notify(data.message, data.code)
                    setTesting(false)
                })
                .then(err => console.log(err))
        }
    }


    const notify = (message, status) => {
        if (status === 1) {
            toast.success(message, {
                position: "top-center",
                autoClose: 3000,
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
                autoClose: 3000,
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
                autoClose: 5000,
                hideProgressBar: false,
                closeOnClick: true,
                draggable: true,
                progress: undefined,
                theme: "colored",
            })
        }

    }
    function findEmptyFields(obj) {
        let emptyFields = [];

        for (let key in obj) {
            if (key !== "id") {
                if (!obj[key]) {
                    if (key !== "note" && (id === null ? true : key !== "password")) emptyFields.push(key);
                }
            }
        }
        return emptyFields;
    }

    return (
        <div className="addModuleScreen">
            <div className="addModule">
                <div className='btn-container d-flex justify-content-end'>
                    <button className='close-btn d-flex align-items-center' onClick={handleCloseWindow}>
                        <FontAwesomeIcon icon={faCircleXmark} />
                    </button>
                </div>
                {/* NAS_Name */}
                <div className="field ">
                    <div className='input_container'>
                        <div className='input_container-icon d-flex align-items-center'>
                            <FontAwesomeIcon icon={faHardDrive} />
                            <div className='input_container-icon-text'>NAS NAME</div>
                        </div>
                        <div className='input_container-input'>
                            <input type='text' id="nasName" placeholder='Type name of NAS...' defaultValue={isEditNas.nasName}></input>
                        </div>
                    </div>
                </div>
                <div className="field ">
                    <div className='input_container'>
                        <div className='input_container-icon d-flex align-items-center'>
                            <FontAwesomeIcon icon={faMapPin} />
                            <div className='input_container-icon-text'>NAS IP</div>
                        </div>
                        <div className='input_container-input'>
                            <input type='text' id="nasIp" placeholder='Type ip address of NAS...' defaultValue={isEditNas.ip}></input>
                        </div>
                    </div>
                </div>
                <div className="field ">
                    <div className='input_container exception'>
                        <div className='input_container-icon d-flex align-items-center'>
                            <FontAwesomeIcon icon={faInbox} />
                            <div className='input_container-icon-text'>PORT</div>
                        </div>
                        <div className='input_container-input'>
                            <input className='' type='text' placeholder='Type port here...' id='port' defaultValue={isEditNas.port}></input>
                        </div>
                    </div>
                </div>
                <div className="field ">
                    <div className='input_container exception'>
                        <div className='input_container-icon d-flex align-items-center'>
                            <FontAwesomeIcon icon={faTerminal} />
                            <div className='input_container-icon-text'>Path</div>
                        </div>
                        <div className='input_container-input'>
                            <input className='' type='text' placeholder='Type path here...' id='path' defaultValue={isEditNas.path}></input>
                        </div>
                    </div>
                </div>
                <div className="field">
                    <div className='input_container'>
                        <div className='input_container-icon d-flex align-items-center'>
                            <FontAwesomeIcon icon={faUser} />
                            <div className='input_container-icon-text'>FTP USERNAME</div>
                        </div>
                        <div className='input_container-input'>
                            <input className=' inputModuleInfo' type='text' placeholder='Type FTP username...' id='fptUsername' defaultValue={isEditNas.username} ></input>
                        </div>
                    </div>
                </div>
                <div className="field">
                    <div className='input_container'>
                        <div className='input_container-icon d-flex align-items-center'>
                            <FontAwesomeIcon icon={faLock} />
                            <div className='input_container-icon-text'>FTP PASSWORD</div>
                        </div>
                        <div className='input_container-input'>
                            <input className=' inputModuleInfo' 
                            type='password' placeholder='Type password here...' 
                            id='fptPassword' 
                            value={fptPassword}  
                            onChange={(e)=>{
                                setFptPassword(e.target.value)
                            }}
                            ></input>
                        </div>
                    </div>
                </div>
                {/*Note*/}
                <div className="field d-flex justify-content-between">
                    <div className='input_container exception'>
                        <div className='input_container-icon d-flex align-items-center'>
                            <FontAwesomeIcon icon={faNoteSticky} />
                            <div className='input_container-icon-text'>NOTE</div>
                        </div>
                        <div className='input_container-input'>
                            <textarea placeholder='Take description here...' id='description' defaultValue={isEditNas.description}></textarea>
                        </div>
                    </div>
                </div>
                {/* Button */}
                <div className='btn-container d-flex justify-content-end'>
                    <button className='btn btn-primary d-flex align-items-center' disabled={testing}  style={{ marginRight: '1rem' }} onClick={testFTPConnection}>
                        <div className='btn-icon d-flex align-items-center' >
                            {!testing && <FontAwesomeIcon icon={faPlay} />}
                            {testing && <FontAwesomeIcon icon={faSpinner} spinPulse style={{color: "#ffffff",}} />}
                        </div>
                        <div className='btn-text' >Test FTP connection</div>
                    </button>
                    <button className='btn btn-success d-flex align-items-center' disabled={testing} onClick={() => addOrEditModule(id)} >
                        <div className='btn-icon d-flex align-items-center' >
                            <FontAwesomeIcon icon={faFloppyDisk} />
                        </div>
                        <div className='btn-text' >Save</div>
                    </button>
                </div>
            </div>
            {confirmScreen && <SmallConfirm setOpenConfirmScreen={setOpenConfirmScreen} object="NAS" action={action} handleFunction={updateNAS} />}
        </div>
    )
}
export default AddNas; 