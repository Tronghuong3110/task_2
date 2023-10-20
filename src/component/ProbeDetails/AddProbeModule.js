import { useState, useEffect } from 'react';
import 'bootstrap/dist/css/bootstrap.css';
import '../../sass/ProbeDetails/AddProbeModule.scss'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faBookmark, faCircleXmark, faFolderOpen, faNoteSticky } from '@fortawesome/free-regular-svg-icons'
import {
    faFloppyDisk, faCube, faTerminal
} from '@fortawesome/free-solid-svg-icons'
import 'react-toastify/dist/ReactToastify.css';
import { ToastContainer, toast } from 'react-toastify';

const AddProbeModule = ({ handleCloseWindow, idProbe, id }) => {

    const [isOpen, openCloseWindow] = useState(true)
    const [listSampleModule, setListSampleModule] = useState([])
    const [commandValue, setCommandValue] = useState({})
    const [isEditedModule, setEditedModule] = useState({})
    const [caption, setCaption] = useState("")
    const [arg, setArg] = useState("")
    useEffect(() => {
        fetch("http://localhost:8081/api/v1/modules")
            .then(response => response.json())
            .then(data => {
                setListSampleModule(data)
                if (id == null) {
                    setCommandValue(data[0].caption + " " + data[0].argDefalt);
                    setCaption(data[0].caption)
                    setArg(data[0].argDefalt)
                    console.log(arg)
                }
            })
            .catch(err => console.log(err))
    }, [])
    useEffect(() => {
        if (id != null) {
            console.log(1)
            fetch("http://localhost:8081/api/v1/probe/module?idProbeModule=" + id)
                .then(response => response.json())
                .then(data => {
                    setEditedModule(data)
                    setCommandValue(data.caption + " " + data.arg);
                })
                .catch(err => console.log(err))
        }

    }, [])

    const addOrEditModule = (id) => {
        let inputData = getModuleInfo(id).inputValue
        let fullData = getModuleInfo(id).fullValue
        if (findEmptyFields(inputData).length != 0) {
            let message = "Field ";
            let arrOption = findEmptyFields(inputData)
            let arr = findEmptyFields(inputData);
            arr = arr.concat(arrOption)
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
                method: fullData.id != null ? "PUT" : "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(fullData)
            }
            if (fullData.id == null) {
                console.log("POST NOW")
                fetch("http://localhost:8081/api/v1/probeModule/import", options)
                    .then(response => response.json())
                    .then(data => {
                        console.log(data)
                        if (data.code == 1) {
                            notify(data.message, data.code)
                            handleCloseWindow()
                        }
                        else if (data.code == 0) {
                            notify(data.message, 0)
                        }
                        else {
                            notify(data.message, 2)
                        }
                    })
                    .catch(err => console.log(err))
            }
            else {
                console.log("PUT NOW")
                fetch("http://localhost:8081/api/v1/probe/module", options)
                    .then(response => response.json())
                    .then(data => {
                        console.log(data)
                        if (data.code == 1) {
                            notify(data.message, 1)
                            handleCloseWindow()
                        }
                        else if (data.code == 0) {
                            notify(data.message, 0)
                        }
                        else {
                            notify(data.message, 2)
                        }
                    })
                    .catch(err => console.log(err))
            }
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
    const onChangeSampleModule = (event) => {
        const selectedOption = event.target.options[event.target.selectedIndex];
        const caption = selectedOption.getAttribute('caption');
        const argDefalt = selectedOption.getAttribute('argdefault');
        setCommandValue(caption + " " + argDefalt);
        setCaption(caption)
        setArg(argDefalt)
    }
    const getModuleInfo = (id) => {
        let idModule = document.querySelector("#idModule")
        let caption = id == null ? idModule.options[idModule.selectedIndex].getAttribute("caption") : isEditedModule.caption
        let listInfo = document.querySelectorAll(".input_container .input_container-input .inputModuleInfo")
        let infoObject = {};
        listInfo.forEach(element => {
            infoObject[element.id] = element.value;
        });
        let probe_module = {
            fullValue: {
                ...infoObject,
                idModule: idModule.value,
                idProbe: idProbe,
                caption: caption,
                id: id
            },
            inputValue: infoObject
        }
        return probe_module;
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
        <div>
            {isOpen && (<div className='addProbeModuleScreen'>
                <div className="addProbe">
                    <div className='btn-container d-flex justify-content-end'>
                        <button className='close-btn d-flex align-items-center' onClick={handleCloseWindow}>
                            <FontAwesomeIcon icon={faCircleXmark} />
                        </button>
                    </div>
                    {/* Module Sample & Modules name  */}
                    <div className="field ">
                        <div className='input_container'>
                            <div className='input_container-icon d-flex align-items-center'>
                                <FontAwesomeIcon icon={faCube} />
                                <div className='input_container-icon-text'>SAMPLE MODULE</div>
                            </div>
                            <div className='input_container-input'>
                                <select disabled={id != null} id='idModule' onChange={onChangeSampleModule} defaultValue={isEditedModule.idModule} >
                                    {
                                        listSampleModule.map(modules => {
                                            return (
                                                <option key={modules.id} caption={modules.caption} argdefault={modules.argDefalt} value={modules.id}>{modules.name}</option>
                                            )
                                        })
                                    }
                                </select>
                            </div>
                        </div>
                        <div className='input_container'>
                            <div className='input_container-icon d-flex align-items-center'>
                                <FontAwesomeIcon icon={faBookmark} />
                                <div className='input_container-icon-text'>MODULE NAME</div>
                            </div>
                            <div className='input_container-input'>
                                <input className='inputModuleInfo' type='text' placeholder='Your module name...' id='moduleName' defaultValue={isEditedModule.moduleName} ></input>
                            </div>
                        </div>
                    </div>
                    {/* Command */}
                    <div className="field ">
                        <div className='input_container exception'>
                            <div className='input_container-icon d-flex align-items-center'>
                                <FontAwesomeIcon icon={faTerminal} />
                                <div className='input_container-icon-text'>COMMAND</div>
                            </div>
                            <div className='input_container-input'>
                                <textarea defaultValue={commandValue} disabled className='commandInput' id='command' ></textarea>
                            </div>
                            <div className='input_container-input'>
                                <input className='commandInput inputModuleInfo' type='text' placeholder='Path...' id='path' defaultValue={isEditedModule.path} ></input>
                            </div>
                            <div className='input_container-input'>
                                <input
                                    className='commandInput exception inputModuleInfo'
                                    type='text' placeholder='Your argument'
                                    id='arg'
                                    defaultValue={id == null ? arg : isEditedModule.arg}
                                    onChange={(e) => {
                                        if (id != null) setCommandValue(isEditedModule.caption + " " + e.target.value)
                                        else setCommandValue(caption + " " + e.target.value)
                                    }}
                                ></input>
                            </div>
                        </div>
                    </div>
                    {/* Log path */}
                    <div className="field">
                        <div className='input_container'>
                            <div className='input_container-icon d-flex align-items-center'>
                                <FontAwesomeIcon icon={faFolderOpen} />
                                <div className='input_container-icon-text'>LOG PATH</div>
                            </div>
                            <div className='input_container-input'>
                                <input className='inputModuleInfo' type='text' placeholder='Log path here....' id='pathLog' defaultValue={isEditedModule.pathLog}></input>
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
                                <textarea className='inputModuleInfo' placeholder='Take note here...' id='note' defaultValue={isEditedModule.note}></textarea>
                            </div>
                        </div>
                    </div>
                    {/* Button */}
                    <div className='btn-container d-flex justify-content-end'>
                        <button className='btn btn-success d-flex align-items-center' onClick={() => {
                            addOrEditModule(id)
                        }} >
                            <div className='btn-icon d-flex align-items-center' >
                                <FontAwesomeIcon icon={faFloppyDisk} />
                            </div>
                            <div className='btn-text' >Save</div>
                        </button>
                    </div>
                </div>
            </div>)
            }
            <ToastContainer></ToastContainer>
        </div>
    )
}
export default AddProbeModule;