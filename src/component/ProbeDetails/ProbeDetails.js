import { useState, useEffect } from "react";
import 'bootstrap/dist/css/bootstrap.css';
import '../../sass/ProbeDetails/ProbeDetails.scss'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faDisplay, faMapPin, faLocationDot, faChartArea, faCube, faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons';
import {
    faFloppyDisk,
    faPenToSquare,
    faStickyNote,
    faSquarePlus
} from '@fortawesome/free-regular-svg-icons';
import DropdownWithInput from "../action/DropdownWithInput";
import Probe_Modules from "./ProbeDetailsTable/Probe_Modules";
import AddProbeModule from './AddProbeModule';
import {  toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { useParams } from "react-router-dom";
import { IP } from "../Layout/constaints";

const ProbeDetails = () => {
    const { id } = useParams();
    const [isOpen, openCloseWindow] = useState(false);
    const [isAppear, setAppear] = useState(false)
    const [location, setLocation] = useState([])
    const [selectedLocation, setSelectedLocation] = useState(null);
    const [selectedArea, setSelectedArea] = useState(null);
    const [area, setArea] = useState([])
    const [probeDetails, setProbeDetails] = useState({})

    //Hàm hiển thị select location và area
    const handleOptionSelectLocation = (selectedValue) => {
        setSelectedLocation(selectedValue);
        let locationChoosen = location.find(element => element.name == selectedValue)
        setSelectedArea(locationChoosen.listArea[0])
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

    //Hàm hiển thị và đóng cửa sổ thêm probe module
    const handleOpenWindow = () => {
        openCloseWindow(true)
    }
    const handleCloseWindow = () => {
        openCloseWindow(false)
    }
    //Hàm hiển thị thông tin probe
    useEffect(() => {
        fetch("http://"+IP+":8081/api/v1/probe?idProbe=" + id)
            .then(response => response.json())
            .then(data => setProbeDetails(data))
            .catch(err => console.log(err))
    }, [])
    //Hàm lấy thông tin sau khi chỉnh sửa probe
    function getInput() {
        let infoInput = []
        let elements = [];
        // Get probe_name

        elements.push(document.getElementById('probe_name'));
        infoInput.push(document.getElementById('probe_name').value);
        elements.push(document.getElementById('ip_address'));
        infoInput.push(document.getElementById('ip_address').value);
        elements.push(document.getElementById('note'));
        infoInput.push(document.getElementById('note').value);
        document.querySelectorAll('.probeDetails .infos .info .select_container .dropdown').forEach(ele => {
            elements.push(ele)
            infoInput.push(ele.textContent)
        });
        let infomation = {
            "infoInput": infoInput,
            "elements": elements
        }
        return infomation;
    }
    //Hàm cho phép edit và save thông tin của Probe
    const handleAllowEditInformations = () => {
        var infomation = getInput();
        var element = infomation.elements;
        element.forEach(ele => {
            ele.removeAttribute('disabled')
            ele.classList.remove('disabled')
        })
        setAppear(!isAppear)
    }
    const getInfomationEdited = () => {
        var infomation = getInput();
        const result = {};
        let keys = ["name", "ipAddress", "description", "location", "area"]
        let values = infomation.infoInput
        for (let i = 0; i < keys.length; i++) {
            result[keys[i]] = values[i];
        }
        return result
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
    const handleSaveInformations = (id) => {
        let data = getInfomationEdited()
        if (findEmptyFields(data) != 0) {
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
            let info = {
                ...getInfomationEdited(),
                id: id
            }
            console.log(info)
            let options = {
                method: "PUT",
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(info)
            }
            fetch("http://"+IP+":8081/api/v1/probe", options)
                .then(response => response.json())
                .then(data => {
                    if (data.code == 1) {
                        notify(data.message, data.code)
                        var infomation = getInput();
                        var element = infomation.elements;
                        element.forEach(ele => {
                            ele.setAttribute('disabled', true);
                            ele.classList.add('disabled')
                        })
                        setAppear(!isAppear)
                    }
                    else {
                        notify(data.message, data.code)
                    }
                })
                .catch(err => console.log(err))
        }

    }
    const setDefaultStatusInput = () => {
        var infomation = getInput();
        var element = infomation.elements;
        element.forEach(ele => {
            ele.setAttribute('disabled', true);
            ele.classList.add('disabled')
        })
    }
    useEffect(() => {
        setDefaultStatusInput()
        setAppear(!isAppear)
    }, [])

    // Hiển thị thông báo
    const notify = (message, status) => {
        if (status == 1) {
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
        else if (status == 0) {
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
                autoClose: 5000,
                hideProgressBar: false,
                closeOnClick: true,
                draggable: true,
                progress: undefined,
                theme: "colored",
            })
        }

    }
    return (
        <div className="probeDetails">
            <div className="infos d-flex justify-content-between align-items-center">
                <div className="info probe_name">
                    <div className="info-title d-flex align-items-center">
                        <div className="info-title-icon">
                            <FontAwesomeIcon icon={faDisplay}></FontAwesomeIcon>
                        </div>
                        <div className="info-title-text">PROBE NAME</div>
                    </div>
                    <div className="info-input">
                        <input defaultValue={probeDetails.name} type="text" id="probe_name"></input>
                    </div>
                </div>
                <div className="info ip_address">
                    <div className="info-title d-flex align-items-center">
                        <div className="info-title-icon">
                            <FontAwesomeIcon icon={faMapPin}></FontAwesomeIcon>
                        </div>
                        <div className="info-title-text">IP ADDRESS</div>
                    </div>
                    <div className="info-input">
                        <input defaultValue={probeDetails.ipAddress} type="text" id="ip_address"></input>
                    </div>
                </div>
                <div className="info location">
                    <div className="info-title d-flex align-items-center">
                        <div className="info-title-icon">
                            <FontAwesomeIcon icon={faLocationDot}></FontAwesomeIcon>
                        </div>
                        <div className="info-title-text">LOCATION</div>
                    </div>
                    <div className="select_container" id="location">
                        <DropdownWithInput defaultValue={probeDetails.location} edit="true" type="Search location" options={location} onOptionSelect={handleOptionSelectLocation} value={selectedLocation} ></DropdownWithInput>
                    </div>

                </div>
                <div className="info area">
                    <div className="info-title d-flex align-items-center">
                        <div className="info-title-icon">
                            <FontAwesomeIcon icon={faChartArea}></FontAwesomeIcon>
                        </div>
                        <div className="info-title-text">AREA</div>
                    </div>
                    <div className="select_container" id="area">
                        <DropdownWithInput defaultValue={probeDetails.area} edit="true" type="Search area" options={area} onOptionSelect={handleOptionSelectArea} value={selectedArea}></DropdownWithInput>
                    </div>

                </div>
            </div>
            <div className="infos d-flex justify-content-between align-items-end">
                <div className="info note">
                    <div className="info-title d-flex align-items-center">
                        <div className="info-title-icon">
                            <FontAwesomeIcon icon={faStickyNote}></FontAwesomeIcon>
                        </div>
                        <div className="info-title-text">DESCRIPTION</div>
                    </div>
                    <div className="info-input">
                        <textarea defaultValue={probeDetails.description} rows={1} id="note"></textarea>
                    </div>
                </div>
                <div className="saveOrEditButton">
                    {isAppear && <button className='btn d-flex align-items-center' onClick={handleAllowEditInformations} >
                        <div className='btn-icon d-flex align-items-center' >
                            <FontAwesomeIcon icon={faPenToSquare} />
                        </div>
                        <div className='btn-text' >Edit</div>
                    </button>}
                    {!isAppear && <button className='btn d-flex align-items-center' onClick={() => {
                        handleSaveInformations(id)
                    }} >
                        <div className='btn-icon d-flex align-items-center' >
                            <FontAwesomeIcon icon={faFloppyDisk} />
                        </div>
                        <div className='btn-text' >Save</div>
                    </button>}
                </div>

            </div>
            
            <Probe_Modules id={id}   ></Probe_Modules>
            {isOpen && <AddProbeModule idProbe={id} id={null} handleCloseWindow={handleCloseWindow}></AddProbeModule>}
        </div>
    )
}
export default ProbeDetails;