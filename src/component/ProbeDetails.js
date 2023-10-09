import { useState, useEffect } from "react";
import 'bootstrap/dist/css/bootstrap.css';
import '../sass/ProbeDetails/ProbeDetails.scss'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faDisplay, faMapPin, faLocationDot, faChartArea, faCube, faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons';
import {
    faFloppyDisk,
    faPenToSquare,
    faStickyNote,
    faSquarePlus
} from '@fortawesome/free-regular-svg-icons';
import DropdownWithInput from "./DropdownWithInput";
import Probe_Modules from "./ProbeDetailsTable/Probe_Modules";
import AddProbeModule from './AddProbeModule';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const ProbeDetails = () => {
    const [isOpen,openCloseWindow] = useState(false);
    const [isAppear, setAppear] = useState(false)
    const [location, setLocation] = useState([])
    const [selectedLocation, setSelectedLocation] = useState(null);
    const [selectedArea, setSelectedArea] = useState(null);
    const [area, setArea] = useState([])

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
        fetch("http://localhost:8081/api/v1/locations")
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
    const handleOpenWindow = () =>{
        openCloseWindow(true)
    }
    const handleCloseWindow = () =>{
        openCloseWindow(false)
    }
    //Hàm lấy thông tin sau khi chỉnh sửa probe
    function getInput() {
        let element = [];
        element.push(document.getElementById('probe_name'));
        element.push(document.getElementById('ip_address'));
        element.push(document.getElementById('note'));
        document.querySelectorAll('.probeDetails .infos .info .select_container .dropdown').forEach(ele => {
            element.push(ele)
        });
        return element;
    }
    //Hàm cho phép edit và save thông tin của Probe
    const handleAllowEditInformations = () => {
        let element = getInput();
        element.forEach(ele => {
            ele.removeAttribute('disabled')
            ele.classList.remove('disabled')
        })
        setAppear(!isAppear)
    }
    const handleSaveInformations = () => {
        var element = getInput();
        element.forEach(ele => {
            ele.setAttribute('disabled', true);
            ele.classList.add('disabled')
        })
        setAppear(!isAppear)
    }
    const setDefaultStatusInput =()=>{
        var element = getInput();
        element.forEach(ele => {
            ele.setAttribute('disabled', true);
            ele.classList.add('disabled')
        })
        console.log(1)
    }
    useEffect(() => {
        setDefaultStatusInput()
        setAppear(!isAppear)
    }, [])

    

    // Hiển thị thông báo
    const notify = () => {
        toast.success("Success", {
            position: "top-center",
            autoClose: 500,
            hideProgressBar: true,
            closeOnClick: true,
            draggable: true,
            progress: undefined,
            theme: "colored",
        })
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
                        <input type="text" id="probe_name"></input>
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
                        <input type="text" id="ip_address"></input>
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
                        <DropdownWithInput  edit="true" type="Search location" options={location} onOptionSelect={handleOptionSelectLocation} value={selectedLocation} ></DropdownWithInput>
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
                        <DropdownWithInput  edit="true" type="Search area" options={area} onOptionSelect={handleOptionSelectArea} value={selectedArea}></DropdownWithInput>
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
                        <textarea rows={1} id="note"></textarea>
                    </div>
                </div>
                <div className="saveOrEditButton">
                    {isAppear && <button className='btn d-flex align-items-center' onClick={handleAllowEditInformations} >
                        <div className='btn-icon d-flex align-items-center' >
                            <FontAwesomeIcon icon={faPenToSquare} />
                        </div>
                        <div className='btn-text' >Edit</div>
                    </button>}
                    {!isAppear && <button className='btn d-flex align-items-center' onClick={handleSaveInformations} >
                        <div className='btn-icon d-flex align-items-center' >
                            <FontAwesomeIcon icon={faFloppyDisk} />
                        </div>
                        <div className='btn-text' >Save</div>
                    </button>}
                </div>
                
            </div>
            <div className="infos">
                <div className="info probe_modules">
                    <div className="info-title d-flex align-items-center">
                        <div className="info-title-icon">
                            <FontAwesomeIcon icon={faCube}></FontAwesomeIcon>
                        </div>
                        <div className="info-title-text">PROBE_MODULES</div>
                    </div>
                    <div className="searchBar d-flex align-items-center">
                        <div className="searchBar-searchName">
                            <div className="searchBar-searchName-input">
                                <input type="text" placeholder="Search "></input>
                                <FontAwesomeIcon icon={faMagnifyingGlass}></FontAwesomeIcon>
                            </div>
                        </div>
                        <div className="searchBar-searchStatus">
                            <div className="searchBar-searchStatus-select">
                                <select>
                                    <option>All</option>
                                    <option>Running</option>
                                    <option>Pending</option>
                                    <option>Stopped</option>
                                    <option>Failed</option>
                                </select>
                            </div>
                        </div>
                        <button className="addBtn d-flex align-items-center" onClick={handleOpenWindow}>
                            <div className="addBtn-icon"><FontAwesomeIcon icon={faSquarePlus}></FontAwesomeIcon></div>
                            <div className="addBtn-text ">New module</div>
                        </button>
                    </div>
                </div>
            </div>
            <Probe_Modules></Probe_Modules>
            {isOpen && <AddProbeModule handleCloseWindow={handleCloseWindow} notify ={notify} ></AddProbeModule>}
            <ToastContainer ></ToastContainer>
        </div>
    )
}

export default ProbeDetails;