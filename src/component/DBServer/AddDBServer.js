import { useState, useEffect } from 'react';
import 'bootstrap/dist/css/bootstrap.css';
import '../../sass/Probes/AddProbe.scss';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCircleXmark, faEye, faEyeSlash, faHardDrive, faNoteSticky } from '@fortawesome/free-regular-svg-icons'
import {
    faMapPin, faLock, faFloppyDisk, faDatabase, faServer, faWindowRestore, faInbox, faUsersViewfinder, faShieldHalved, faPlay, faSpinner
} from '@fortawesome/free-solid-svg-icons'
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { IP } from '../Layout/constaints';
import SmallConfirm from '../action/SmallConfirm';

const AddDBServer = ({ handleCloseWindow, id, rerender }) => {
    const [isOpen, openCloseAddWindow] = useState(true)
    const [confirmScreen, setOpenConfirmScreen] = useState(false)
    const [passwordType1, setPasswordType1] = useState(true)
    const [passwordType2, setPasswordType2] = useState(true)
    const [passwordSudo, setPasswordSudo] = useState(true)
    const [nasList, setNasList] = useState([])
    const [action, setAction] = useState({
        "module": null,
        "action": "update",
        "note": ""
    })
    const [isEditedServer, setEditedServer] = useState(
        {
            "id": id,
            "ipServer": "",
            "serverName": "",
            "type": "SSD",
            "description": "",
            "dbAccount": "",
            "dbPass": "",
            "sshAccount": "",
            "sshPass": "",
            "nasId": null,
            "nasName": "",
            "portNumber": 8123
        }


    )
    const [testingDB, setTestingDB] = useState(false)
    const [testingSSH, setTestingSSH] = useState(false)
    const [saving, setSaving] = useState(false)

    useEffect(() => {
        if (id !== null) {
            fetch(IP + "/api/v1/database/server?id=" + id)
                .then(response => response.json())
                .then(data => setEditedServer(data))
                .catch(err => console.log(err))
        }
    }, [])
    useEffect(() => {
        fetch(IP + "/api/v1/nases")
            .then(response => response.json())
            .then(data => {
                setNasList(data)
            })
            .catch(err => console.log(err))
    }, [])
    useEffect(() => {

    }, [])
    const setPasswordType = (value) => {
        if (value === true) return 'password'
        else return 'text'
    }

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
    const addOrEditDBServer = (id) => {
        let data = getDBServerInfo();
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
            setSaving(true)
            console.log(data)
            let options = {
                method: id === null ? "POST" : "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(data)
            }
            if (id === null) {
                fetch(IP + "/api/v1/database/import", options)
                    .then(response => response.json())
                    .then(data => {
                        if (data.code === 1) {
                            notify(data.message, data.code)
                            setSaving(false)
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
        console.log(data)
    }
    const updateDBServer = (id, action) => {
        let data = getDBServerInfo();
        let options = {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        }
        fetch(IP + "/api/v1/database/server", options)
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
    const getDBServerInfo = () => {
        return {
            "id": id,
            "ipServer": document.getElementById("ipServer").value,
            "serverName": document.getElementById("serverName").value,
            "type": document.getElementById("type").options[document.getElementById("type").selectedIndex].value,
            "description": document.getElementById("description").value,
            "dbAccount": document.getElementById("dbAccount").value,
            "dbPass": document.getElementById("dbPass").value,
            "sshAccount": document.getElementById("sshAccount").value,
            "sshPass": document.getElementById("sshPass").value,
            "nasId": document.getElementById("nasId").options[document.getElementById("nasId").selectedIndex].value,
            "nasName": document.getElementById("nasId").options[document.getElementById("nasId").selectedIndex].textContent,
            "portNumber": document.getElementById("portNumber").value,
            'passSudo': document.getElementById("passSudo").value
        }
    }
    function findEmptyFields(obj) {
        let emptyFields = [];

        for (let key in obj) {
            if (key !== "id") {
                if (!obj[key]) {
                    if (key !== "description") emptyFields.push(key);
                }
            }
        }

        return emptyFields;
    }
    // Các hàm test connection

    const testDbConnection = () => {
        let dataTestConnection = {
            "ipServer": document.getElementById("ipServer").value,
            "portNumber": document.getElementById("portNumber").value,
            "dbAccount": document.getElementById("dbAccount").value,
            "dbPass": document.getElementById("dbPass").value
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
            setTestingDB(true)
            let options = {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(dataTestConnection)
            }
            fetch(IP + "/api/v1/test/connect/database", options)
                .then(response => response.json())
                .then(data => {
                    notify(data.message, data.code)
                    setTestingDB(false)
                })
                .then(err => console.log(err))
        }
    }
    const testSSHConnection = () => {
        let dataTestConnection = {
            "ipServer": document.getElementById("ipServer").value,
            "sshPort": 22,
            "sshAccount": document.getElementById("sshAccount").value,
            "sshPass": document.getElementById("sshPass").value
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
            setTestingSSH(true)
            let options = {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(dataTestConnection)
            }
            fetch(IP + "/api/v1/test/connect/ssh", options)
                .then(response => response.json())
                .then(data => {
                    notify(data.message, data.code)
                    setTestingSSH(false)
                })
                .then(err => console.log(err))
        }
    }
    return (
        <div>
            {isOpen && (<div className='addProbeScreen'>
                <div className="addProbe">
                    <div className='btn-container d-flex justify-content-end'>
                        <button className='close-btn d-flex align-items-center' onClick={handleCloseWindow}>
                            <FontAwesomeIcon icon={faCircleXmark} />
                        </button>
                    </div>
                    {/* Server IP & DB Account */}
                    <div className="field d-flex justify-content-between">
                        <div className='input_container'>
                            <div className='input_container-icon d-flex align-items-center'>
                                <FontAwesomeIcon icon={faMapPin} />
                                <div className='input_container-icon-text'>SERVER IP</div>
                            </div>
                            <div className='input_container-input'>
                                <input id='ipServer' type='text' placeholder='Type server ip....' defaultValue={isEditedServer.ipServer}></input>
                            </div>
                        </div>
                        <div className='input_container'>
                            <div className='input_container-icon d-flex align-items-center'>
                                <FontAwesomeIcon icon={faDatabase} />
                                <div className='input_container-icon-text'>DATABASE ACCOUNT</div>
                            </div>
                            <div className='input_container-input'>
                                <input type='text' placeholder='Type database account here....' id="dbAccount" defaultValue={isEditedServer.dbAccount}></input>
                            </div>
                        </div>
                    </div>
                    {/* Server name & DB Password */}
                    <div className="field d-flex justify-content-between">
                        <div className='input_container'>
                            <div className='input_container-icon d-flex align-items-center'>
                                <FontAwesomeIcon icon={faServer} />
                                <div className='input_container-icon-text'>SERVER NAME</div>
                            </div>
                            <div className='input_container-input'>
                                <input type='text' placeholder='Type servername ....' id='serverName' defaultValue={isEditedServer.serverName}></input>
                            </div>
                        </div>
                        <div className='input_container'>
                            <div className='input_container-icon d-flex align-items-center'>
                                <FontAwesomeIcon icon={faLock} />
                                <div className='input_container-icon-text'>DATABASE PASSWORD</div>
                            </div>
                            <div className='input_container-input'>
                                <input type={setPasswordType(passwordType1).toString()} placeholder='Type password ....' id='dbPass' defaultValue={isEditedServer.dbPass}></input>
                                {passwordType1 === true ?
                                    (<FontAwesomeIcon icon={faEyeSlash} style={{ color: "#ffffff", }} className='eye' onClick={() => setPasswordType1(false)} />)
                                    : (<FontAwesomeIcon icon={faEye} style={{ color: "#ffffff" }} className='eye' onClick={() => setPasswordType1(true)} />)}
                            </div>
                        </div>
                    </div>
                    {/* Server type & DB PORT */}
                    <div className="field d-flex justify-content-between">
                        <div className='input_container'>
                            <div className='input_container-icon d-flex align-items-center'>
                                <FontAwesomeIcon icon={faWindowRestore} />
                                <div className='input_container-icon-text'>SERVER TYPE</div>
                            </div>
                            <div className='input_container-input'>
                                <select id='type'>
                                    <option value="SSD" selected={isEditedServer.type === "SSD"}>SSD</option>
                                    <option value="HDD" selected={isEditedServer.type === "HDD"} >HDD</option>
                                </select>
                            </div>
                        </div>
                        <div className='input_container'>
                            <div className='input_container-icon d-flex align-items-center'>
                                <FontAwesomeIcon icon={faInbox} />
                                <div className='input_container-icon-text'>DATABASE PORT</div>
                            </div>
                            <div className='input_container-input'>
                                <input type='text' placeholder='Type database port ....' id='portNumber' defaultValue={isEditedServer.portNumber}></input>
                            </div>
                        </div>
                    </div>
                    {/* Backup NAS & SSH Account */}
                    <div className="field d-flex justify-content-between">
                        <div className='input_container'>
                            <div className='input_container-icon d-flex align-items-center'>
                                <FontAwesomeIcon icon={faHardDrive} />
                                <div className='input_container-icon-text'>BACKUP NAS</div>
                            </div>
                            <div className='input_container-input'>
                                <select id='nasId'>
                                    {
                                        nasList.map(nas => {
                                            return (
                                                <option selected={nas.id === isEditedServer.nasId} value={nas.id}>{nas.nasName}</option>
                                            )
                                        })
                                    }
                                </select>
                            </div>
                        </div>
                        <div className='input_container'>
                            <div className='input_container-icon d-flex align-items-center'>
                                <FontAwesomeIcon icon={faUsersViewfinder} />
                                <div className='input_container-icon-text'>SSH ACCOUNT</div>
                            </div>
                            <div className='input_container-input'>
                                <input type='text' placeholder='Type ssh account ....' id='sshAccount' defaultValue={isEditedServer.sshAccount}></input>
                            </div>
                        </div>
                    </div>
                    {/* SUDO Password & SSH Password */}
                    <div className="field d-flex justify-content-between">
                        {/* <div className='input_container'>
                            <div className='input_container-icon d-flex align-items-center'>
                                <FontAwesomeIcon icon={faNoteSticky} />
                                <div className='input_container-icon-text'>DESCRIPTION</div>
                            </div>
                            <div className='input_container-input'>
                                <input type='text' placeholder='Type description ....' id='description' defaultValue={isEditedServer.description}></input>
                            </div>
                        </div> */}
                        <div className='input_container'>
                            <div className='input_container-icon d-flex align-items-center'>
                                <FontAwesomeIcon icon={faShieldHalved} />
                                <div className='input_container-icon-text'>SUDO PASSWORD</div>
                            </div>
                            <div className='input_container-input'>
                                <input type={setPasswordType(passwordSudo).toString()} placeholder='Type sudo password ....' id='passSudo' defaultValue={isEditedServer.passSudo}></input>
                                {passwordSudo === true ?
                                    (<FontAwesomeIcon icon={faEyeSlash} style={{ color: "#ffffff", }} className='eye' onClick={() => setPasswordSudo(false)} />)
                                    : (<FontAwesomeIcon icon={faEye} style={{ color: "#ffffff" }} className='eye' onClick={() => setPasswordSudo(true)} />)}
                            </div>
                        </div>
                        <div className='input_container'>
                            <div className='input_container-icon d-flex align-items-center'>
                                <FontAwesomeIcon icon={faShieldHalved} />
                                <div className='input_container-icon-text'>SSH PASSWORD</div>
                            </div>
                            <div className='input_container-input'>
                                <input type={setPasswordType(passwordType2).toString()} placeholder='Type ssh password ....' id='sshPass' defaultValue={isEditedServer.sshPass}></input>
                                {passwordType2 === true ?
                                    (<FontAwesomeIcon icon={faEyeSlash} style={{ color: "#ffffff", }} className='eye' onClick={() => setPasswordType2(false)} />)
                                    : (<FontAwesomeIcon icon={faEye} style={{ color: "#ffffff" }} className='eye' onClick={() => setPasswordType2(true)} />)}
                            </div>
                        </div>
                    </div>
                    {/*Description*/}
                    <div className="field d-flex justify-content-between">
                        <div className='input_container exception'>
                            <div className='input_container-icon d-flex align-items-center'>
                                <FontAwesomeIcon icon={faNoteSticky} />
                                <div className='input_container-icon-text'>DESCRIPTION</div>
                            </div>
                            <div className='input_container-input'>
                                <textarea placeholder='Take description here...' id='description' defaultValue={isEditedServer.description}></textarea>
                            </div>
                        </div>
                    </div>
                    {/* Button */}
                    <div className='btn-container d-flex justify-content-end'>
                        <button className='btn btn-danger d-flex align-items-center'
                            style={{ marginRight: '1rem' }}
                            onClick={testDbConnection}
                            disabled={testingDB || testingSSH ||saving}
                        >
                            <div className='btn-icon d-flex align-items-center' >
                                {!testingDB && <FontAwesomeIcon icon={faPlay} />}
                                {testingDB && <FontAwesomeIcon icon={faSpinner} spinPulse style={{ color: "#ffffff", }} />}
                            </div>
                            <div className='btn-text' >Test Database connection</div>
                        </button>
                        <button className='btn btn-primary d-flex align-items-center'
                            style={{ marginRight: '1rem' }}
                            onClick={testSSHConnection}
                            disabled={testingDB || testingSSH ||saving}
                        >
                            <div className='btn-icon d-flex align-items-center' >
                                {!testingSSH && <FontAwesomeIcon icon={faPlay} />}
                                {testingSSH && <FontAwesomeIcon icon={faSpinner} spinPulse style={{ color: "#ffffff", }} />}
                            </div>
                            <div className='btn-text' >Test SSH connection</div>
                        </button>
                        <button className='btn btn-success d-flex align-items-center'
                            onClick={() => addOrEditDBServer(id)}
                            disabled={testingDB || testingSSH ||saving}
                        >
                            <div className='btn-icon d-flex align-items-center' >
                                {!saving && <FontAwesomeIcon icon={faFloppyDisk} />}
                                {saving && <FontAwesomeIcon icon={faSpinner} spinPulse style={{ color: "#ffffff", }} />}
                            </div>
                            <div className='btn-text' >Save</div>
                        </button>
                    </div>
                </div>
            </div>)
            }
            {confirmScreen && <SmallConfirm setOpenConfirmScreen={setOpenConfirmScreen} action={action} object="database server" handleFunction={updateDBServer} />}
        </div >
    )
}
export default AddDBServer;