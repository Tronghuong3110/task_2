import { React, useState, useEffect, useContext } from "react";
import 'bootstrap/dist/css/bootstrap.css';
import '../../sass/Probes/Probes.scss';
import Button from "@mui/material/Button"
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
    faCopy,
    faMagnifyingGlass
} from '@fortawesome/free-solid-svg-icons';
import {
    faSquarePlus
} from '@fortawesome/free-regular-svg-icons';
import DropdownWithInput from "../action/DropdownWithInput";
import ProbesTable from "./ProbesTable/ProbesTable";
import AddProbe from './AddProbe';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { ProbesProvider, ProbesContext } from "./ProbesTable/ProbesContext";
import Confirm from '../action/Confirm'
import { IP } from "../Layout/constaints";
import DuplicateProbe from './DuplicateProbe'

const Probes = () => {
    const [isOpenAddWindow, openCloseAddWindow] = useState(false);
    const [isOpenDuplicateWindow, openCloseDuplicateWindow] = useState(false);
    const [location, setLocation] = useState([])
    const [selectedLocation, setSelectedLocation] = useState(null);
    const [selectedArea, setSelectedArea] = useState(null);
    const [area, setArea] = useState([])
    const probesContext = useContext(ProbesContext)
    //Hàm hiển thị select location và area
    useEffect(() => {
        fetch("http://"+IP+":8081/api/v1/locations")
            .then(response => response.json())
            .then(data => setLocation(data))
            .catch(err => console.log(err))
    }, [])
    useEffect(() => {
        if (selectedLocation) {
            if(selectedLocation=="---.---"){
                setArea([])
            }
            else{
                let locationChoosen = location.find(element => element.name == selectedLocation)
                setArea(locationChoosen.listArea)
            }
        }
    }, [selectedLocation])
    useEffect(()=>{
        document.querySelector(".header-name").textContent = "PROBES"
    },[])
    const handleOptionSelectLocation = (selectedValue) => {
        setSelectedLocation(selectedValue);
        setSelectedArea(null)
    };
    const handleOptionSelectArea = (selectedValue) => {
        setSelectedArea(selectedValue);
    };
    // Hàm đóng mở cửa sổ thêm mới Probe
    const handleOpenAddWindow = () => {
        openCloseAddWindow(true)
    }
    const handleCloseAddWindow = () => {
        openCloseAddWindow(false)
    }
    // Hàm đóng mở cửa sổ  nhan ban Probe
    const handleOpenDuplicateWindow = () => {
        openCloseDuplicateWindow(true)
    }
    const handleCloseDuplicateWindow = () => {
        openCloseDuplicateWindow(false)
    }
    //Tim theo dieu kien
    const findByCondition = () => {
        let conditions = {
            "name": document.getElementById("name").value,
            "location": (document.querySelector("#location .dropdown .select").textContent === 'Search location' || document.querySelector("#location .dropdown .select").textContent === '---.---') ? '' : document.querySelector("#location .dropdown .select").textContent,
            "area": (document.querySelector("#area .dropdown .select").textContent === 'Search area' || document.querySelector("#area .dropdown .select").textContent === '---.---') ? '' : document.querySelector("#area .dropdown .select").textContent,
            "vlan": document.querySelector("#vlan .dropdown .select").textContent==='Search VLAN'||'---.---'?'':document.querySelector("#vlan .dropdown .select").textContent
        }
        probesContext.setConditions(conditions)
    }
    return (
        <div className="probes">
            <div className="probes-action-buttonAdd d-flex">
                <Button className="addProbe-btn" style={{padding: "7px 15px" }} onClick={handleOpenAddWindow}>
                    <FontAwesomeIcon icon={faSquarePlus} style={{ color: "#ffffff"}} />
                    <div className="btn-text">Add probes</div>
                </Button>
                <Button className="addProbe-btn" onClick={handleOpenDuplicateWindow} style={{marginLeft:"20px",padding: "7px 15px" }}> 
                    <FontAwesomeIcon icon={faCopy} style={{ color: "#ffffff" }} />
                    <div className="btn-text">Duplicate</div>
                </Button>
            </div>
            <div className="probes-action d-flex justify-content-between">
                <div className="probes-action-input">
                    <input type="text" placeholder='Search by name...' id="name" />
                    <div className='search-icon'>
                        <FontAwesomeIcon icon={faMagnifyingGlass} rotation={90} style={{ color: "#ffffff", }} />
                    </div>
                </div>
                <div className="probes-action-select" id="location">
                    <DropdownWithInput type="Search location" options={location} onOptionSelect={handleOptionSelectLocation} value={selectedLocation} ></DropdownWithInput>
                </div>
                <div className="probes-action-select" id="area">
                    <DropdownWithInput type="Search area" options={area} onOptionSelect={handleOptionSelectArea} value={selectedArea}></DropdownWithInput>
                </div>
                <div className="probes-action-select" id="vlan">
                    <DropdownWithInput type="Search VLAN" options={[]}></DropdownWithInput>
                </div>
                <div className="probes-action-buttonSearch">
                    <Button className="search-btn" onClick={findByCondition}>
                        <FontAwesomeIcon icon={faMagnifyingGlass} rotation={90} style={{ color: "#ffffff", }} />
                        <div className="btn-text">Search</div>
                    </Button>
                </div>
            </div>
            <ProbesTable></ProbesTable>
            {isOpenAddWindow && <AddProbe handleCloseWindow={handleCloseAddWindow} ></AddProbe>}
            {isOpenDuplicateWindow && <DuplicateProbe handleCloseWindow = {handleCloseDuplicateWindow}></DuplicateProbe>}
            <ToastContainer ></ToastContainer>
            {probesContext.openDeleteScreen && <Confirm confirmContent={probesContext.deletedProbe} ></Confirm>}
        </div>
    );
}

export default Probes;