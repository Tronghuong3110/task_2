import { useState, useEffect, useContext } from 'react';
import 'bootstrap/dist/css/bootstrap.css';
import '../../sass/Probes/AddProbe.scss';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCircleXmark, faNoteSticky } from '@fortawesome/free-regular-svg-icons'
import {
    faDisplay, faLocationDot, faUser, faCaretDown, faChartArea, faMapPin, faLock, faBolt, faWifi, faSeedling, faBroom, faBan, faFloppyDisk
} from '@fortawesome/free-solid-svg-icons'
import DropdownWithInput from '../action/DropdownWithInput';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { ProbesContext } from './ProbesTable/ProbesContext';
import { IP } from '../Layout/constaints';

const AddProbe = ({ handleCloseWindow }) => {
    const [isOpen, openCloseWindow] = useState(true)
    const [location, setLocation] = useState([])
    const [selectedLocation, setSelectedLocation] = useState(null);
    const [selectedArea, setSelectedArea] = useState(null);
    const [area, setArea] = useState([])

    const probesContext = useContext(ProbesContext);

    //Hàm hiển thị select location và area
    const handleOptionSelectLocation = (selectedValue) => {
        setSelectedLocation(selectedValue);
        setSelectedArea(null)
    };
    const handleOptionSelectArea = (selectedValue) => {
        setSelectedArea(selectedValue);
    };
    useEffect(() => {
        fetch("http://"+IP+":8081/api/v1/locations")
            .then(response => response.json())
            .then(data => setLocation(data))
            .catch(err => console.log(err))
    }, [])
    useEffect(() => {
        if (selectedLocation) {
            if (selectedLocation == "---.---") {
                setArea([])
            }
            else {
                let locationChoosen = location.find(element => element.name == selectedLocation)
                setArea(locationChoosen.listArea)
            }
        }
    }, [selectedLocation])

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
    //Hàm thêm probe
    const handleAddProbe = () => {
        let probe = getProbe();
        let options = getProbeOptions()
        console.log(probe,options)
        if (findEmptyFields(probe).length != 0 || findEmptyFields(options).length !=0 ) {
            let message = "Field ";
            let arrOption = findEmptyFields(options)
            let arr = findEmptyFields(probe);
            arr= arr.concat(arrOption)
            if (arr.length == 1) message += arr[0] + " is empty"
            else {
                for (let i = 0; i < arr.length; i++) {
                    if (i != arr.length - 1) message += arr[i] + ", "
                    else message += arr[i]
                }
                message+= " are empty"
            }
            notify(message,2)
        }   
        else {
            let formData = {
                "probeDto": probe,
                "probeOptionDto": options
            }
            const requestOptions = {
                method: 'POST',
                headers: {
                    "Content-Type": "application/json; charset=utf8"
                },
                body: JSON.stringify(formData)
            };
            fetch("http://"+IP+":8081/api/v1/probe/import", requestOptions)
                .then(response => response.json())
                .then(response => {
                    let newData = [...probesContext.probes, response];
                    if (response.message === 'Create probe success') {
                        probesContext.setProbes(newData)
                        console.log(probesContext.probes)
                        notify(response.message, 1)
                        handleCloseWindow()
                    }
                    else {
                        notify(response.message, 0)
                    }
                })
                .catch(err => console.log(err))

        }
    }
    const getProbe = () => {
        let probe = {
            "name": document.getElementById("probe_name").value,
            "ipAddress": document.getElementById("ip_address").value,
            "olt": document.getElementById("olt").value,
            "vlan": document.getElementById("vlanInput").value,
            "description": document.getElementById("note").value,
            "location": (document.querySelector("#locationInput .dropdown .select").textContent === 'Choose the location' || document.querySelector("#locationInput .dropdown .select").textContent === '---.---') ? '' : document.querySelector("#locationInput .dropdown .select").textContent,
            "area": (document.querySelector("#areaInput .dropdown .select").textContent === 'Choose the area' || document.querySelector("#areaInput .dropdown .select").textContent === '---.---') ? '' : document.querySelector("#areaInput .dropdown .select").textContent,
        }
        return probe;
    }
    const getProbeOptions = () => {
        let options = {
            "username": document.getElementById("username").value,
            "password": document.getElementById("password").value,
            "keepAlive": document.getElementById("keep_alive").value,
            "cleanSession": document.getElementById("clean-session").value,
            "connectionTimeout": document.getElementById("connection_timeout").value
        }
        return options;
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
            {isOpen && (<div className='addProbeScreen'>
                <div className="addProbe">
                    <div className='btn-container d-flex justify-content-end'>
                        <button className='close-btn d-flex align-items-center' onClick={handleCloseWindow}>
                            <FontAwesomeIcon icon={faCircleXmark} />
                        </button>
                    </div>
                    {/* Probe name & User Name */}
                    <div className="field d-flex justify-content-between">
                        <div className='input_container'>
                            <div className='input_container-icon d-flex align-items-center'>
                                <FontAwesomeIcon icon={faDisplay} />
                                <div className='input_container-icon-text'>PROBE NAME</div>
                            </div>
                            <div className='input_container-input'>
                                <input id='probe_name' type='text' placeholder='Type probe name....'></input>
                            </div>
                        </div>
                        <div className='input_container'>
                            <div className='input_container-icon d-flex align-items-center'>
                                <FontAwesomeIcon icon={faUser} />
                                <div className='input_container-icon-text'>USERNAME</div>
                            </div>
                            <div className='input_container-input'>
                                <input type='text' placeholder='Type username here....' id="username"></input>
                            </div>
                        </div>
                    </div>
                    {/* IP Address & Password */}
                    <div className="field d-flex justify-content-between">
                        <div className='input_container'>
                            <div className='input_container-icon d-flex align-items-center'>
                                <FontAwesomeIcon icon={faMapPin} />
                                <div className='input_container-icon-text'>IP ADDRESS</div>
                            </div>
                            <div className='input_container-input'>
                                <input type='text' placeholder='Type ip address ....' id='ip_address'></input>
                            </div>
                        </div>
                        <div className='input_container'>
                            <div className='input_container-icon d-flex align-items-center'>
                                <FontAwesomeIcon icon={faLock} />
                                <div className='input_container-icon-text'>PASSWORD</div>
                            </div>
                            <div className='input_container-input'>
                                <input type='text' placeholder='Type password ....' id='password'></input>
                            </div>
                        </div>
                    </div>
                    {/*Location Area & OLP VLAN*/}
                    <div className="field d-flex justify-content-between">
                        {/*Location & Area*/}
                        <div className='input_container d-flex justify-content-between'>
                            <div className='select_container'>
                                <div className='input_container-icon d-flex align-items-center'>
                                    <FontAwesomeIcon icon={faLocationDot} />
                                    <div className='input_container-icon-text'>LOCATION</div>
                                </div>
                                <div className='input_container-input' id="locationInput">
                                    <DropdownWithInput className="input_container-select" type="Choose the location" options={location} onOptionSelect={handleOptionSelectLocation} value={selectedLocation} ></DropdownWithInput>
                                    <FontAwesomeIcon className='down-arrow' icon={faCaretDown} />
                                </div>
                            </div>
                            <div className='select_container'>
                                <div className='input_container-icon d-flex align-items-center'>
                                    <FontAwesomeIcon icon={faChartArea} />
                                    <div className='input_container-icon-text'>AREA</div>
                                </div>
                                <div className='input_container-input' id="areaInput">
                                    <DropdownWithInput className="input_container-select" type="Choose the area" options={area} onOptionSelect={handleOptionSelectArea} value={selectedArea}></DropdownWithInput>
                                    <FontAwesomeIcon className='down-arrow' icon={faCaretDown} />
                                </div>
                            </div>
                        </div>
                        {/*Olt & Vlan*/}
                        <div className='input_container d-flex justify-content-between'>
                            <div className='input_container-mini'>
                                <div className='input_container-icon d-flex align-items-center'>
                                    <FontAwesomeIcon icon={faBolt} />
                                    <div className='input_container-icon-text'>OLT</div>
                                </div>
                                <div className='input_container-input'>
                                    <input type='text' placeholder='Type OLT ....' id="olt"></input>
                                </div>
                            </div>
                            <div className='input_container-mini'>
                                <div className='input_container-icon d-flex align-items-center'>
                                    <FontAwesomeIcon icon={faWifi} />
                                    <div className='input_container-icon-text'>VLAN</div>
                                </div>
                                <div className='input_container-input'>
                                    <input type='text' placeholder='Type VLAN ....' id='vlanInput' ></input>
                                </div>
                            </div>
                        </div>
                    </div>
                    {/*Keep alive Clean session & Connection timeout*/}
                    <div className="field d-flex justify-content-between">
                        {/*Keep alive & Clean session*/}
                        <div className='input_container d-flex justify-content-between'>
                            <div className='input_container-mini'>
                                <div className='input_container-icon d-flex align-items-center'>
                                    <FontAwesomeIcon icon={faSeedling} />
                                    <div className='input_container-icon-text'>KEEP ALIVE(s)</div>
                                </div>
                                <div className='input_container-input'>
                                    <input type='text' defaultValue={3} id='keep_alive'></input>
                                </div>
                            </div>
                            <div className='input_container-mini'>
                                <div className='input_container-icon d-flex align-items-center'>
                                    <FontAwesomeIcon icon={faBroom} />
                                    <div className='input_container-icon-text'>CLEAN SESSION</div>
                                </div>
                                <div className='input_container-input'>
                                    <select name='clean-session' id='clean-session'>
                                        <option>true</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                        {/*Connection Timeout*/}
                        <div className='input_container d-flex justify-content-between'>
                            <div className='input_container-mini exception'>
                                <div className='input_container-icon d-flex align-items-center'>
                                    <FontAwesomeIcon icon={faBan} />
                                    <div className='input_container-icon-text'>CONNECTION TIMEOUT(s)</div>
                                </div>
                                <div className='input_container-input'>
                                    <input type='text' placeholder='Type connection timeout ....' defaultValue={10} id='connection_timeout'></input>
                                </div>
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
                                <textarea placeholder='Take note here...' id='note'></textarea>
                            </div>
                        </div>
                    </div>
                    {/* Button */}
                    <div className='btn-container d-flex justify-content-end'>
                        <button className='btn btn-success d-flex align-items-center' onClick={handleAddProbe}>
                            <div className='btn-icon d-flex align-items-center' >
                                <FontAwesomeIcon icon={faFloppyDisk} />
                            </div>
                            <div className='btn-text' >Save</div>
                        </button>
                    </div>
                </div>
            </div>)
            }

        </div>
    )
}
export default AddProbe;