import { useState, useEffect } from 'react';
import 'bootstrap/dist/css/bootstrap.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCircleXmark, faFolderOpen, faNoteSticky } from '@fortawesome/free-regular-svg-icons'
import {
    faFloppyDisk, faCube, faTerminal
} from '@fortawesome/free-solid-svg-icons'
import '../../sass/Module/AddModule.scss';
import { toast } from 'react-toastify'
import { IP } from '../Layout/constaints';
const AddModule = ({ handleCloseWindow, id }) => {
    const [isEditModule, setEditModule] = useState({
        "name": "",
        "path": "",
        "caption": "",
        "argDefalt": "",
        "note": ""
    })

    useEffect(() => {
        if (id != null) {
            fetch("http://"+IP+":8081/api/v1/module?idModule=" + id)
                .then(response => response.json())
                .then(data => setEditModule(data))
                .catch(err => console.log(err))
        }
    }, [])

    // Thêm mới một module mẫu
    const addNewModule = (id) => {
        let data = getModuleInfo();
        if (findEmptyFields(data).length > 0) {
            let message = "Field ";
            let arr = findEmptyFields(data);
            if (arr.length == 1) message += arr[0] + " is empty"
            else {
                for (let i = 0; i < arr.length; i++) {
                    if (i != arr.length - 1) message += arr[i] + ", "
                    else message += arr[i]
                }
                message += " are empty"
            }
            notify(message, 2)
        }
        else {
            let options = {
                method: id == null ? "POST" : "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(data)
            }
            if (id == null) {
                fetch("http://"+IP+":8081/api/v1/module/import", options)
                    .then(response => response.json())
                    .then(data => {
                        if(data.code == 1){
                            notify(data.message, data.code)
                            handleCloseWindow();
                        }
                        else notify(data.message,data.code)
                    })
                    .catch(err => console.log(err))
            }
            else {
                fetch("http://"+IP+":8081/api/v1/module", options)
                    .then(response => response.json())
                    .then(data => {
                        if(data.code == 1){
                            notify(data.message, data.code)
                            handleCloseWindow();
                        }
                        else notify(data.message,data.code)
                    })
                    .catch(err => console.log(err))
            }
        }
    }
    const getModuleInfo = () => {
        return {
            "name": document.getElementById("module_name").value,
            "path": document.getElementById("path").value,
            "pathLog": document.getElementById("pathLog").value,
            "caption": document.getElementById("caption").value,
            "argDefalt": document.getElementById("argument").value,
            "note": document.getElementById("note").value
        }
    }
    const notify = (message, status) => {
        if (status == 1) {
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
        else if (status == 0) {
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
            if (!obj[key]) {
                emptyFields.push(key);
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
                {/* Module_Name */}
                <div className="field ">
                    <div className='input_container'>
                        <div className='input_container-icon d-flex align-items-center'>
                            <FontAwesomeIcon icon={faCube} />
                            <div className='input_container-icon-text'>MODULE</div>
                        </div>
                        <div className='input_container-input'>
                            <input type='text' id="module_name" placeholder='Type module name...' value={isEditModule.name}></input>
                        </div>
                    </div>
                    <div className="field ">
                        <div className='input_container exception'>
                            <div className='input_container-icon d-flex align-items-center'>
                                <FontAwesomeIcon icon={faTerminal} />
                                <div className='input_container-icon-text'>COMMAND</div>
                            </div>
                            <div className='input_container-input'>
                                <input className='commandInput' type='text' placeholder='Caption...' id='caption' value={isEditModule.caption}></input>
                            </div>
                            <div className='input_container-input'>
                                <input className='commandInput exception' type='text' placeholder='Default argument' id='argument' value={isEditModule.argDefalt}></input>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="field">
                    <div className='input_container'>
                        <div className='input_container-icon d-flex align-items-center'>
                            <FontAwesomeIcon icon={faFolderOpen} />
                            <div className='input_container-icon-text'>PATH AND LOG PATH</div>
                        </div>
                        <div className='input_container-input'>
                            <input className='commandInput inputModuleInfo' type='text' placeholder='Path...' id='path' defaultValue={isEditModule.pathDefault} ></input>
                        </div>
                        <div className='input_container-input'>
                            <input className='inputModuleInfo' type='text' placeholder='Log path here....' id='pathLog' defaultValue={isEditModule.pathLogDefault}></input>
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
                            <textarea placeholder='Take note here...' id='note' value={isEditModule.note}></textarea>
                        </div>
                    </div>
                </div>
                {/* Button */}
                <div className='btn-container d-flex justify-content-end'>
                    <button className='btn btn-success d-flex align-items-center' onClick={addNewModule} >
                        <div className='btn-icon d-flex align-items-center' >
                            <FontAwesomeIcon icon={faFloppyDisk} />
                        </div>
                        <div className='btn-text' >Save</div>
                    </button>
                </div>
            </div>
        </div>
    )
}
export default AddModule; 