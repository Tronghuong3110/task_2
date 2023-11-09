import { React, useState, useEffect, useContext } from "react";
import 'bootstrap/dist/css/bootstrap.css';
import '../../sass/Probes/Probes.scss';
import Button from "@mui/material/Button"
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
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

const Probes = () => {
    const [isOpen, openCloseWindow] = useState(false);
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
    const handleOpenWindow = () => {
        openCloseWindow(true)
    }
    const handleCloseWindow = () => {
        openCloseWindow(false)
    }
    //Tim theo dieu kien
    const findByCondition = () => {
        let conditions = {
            "name": document.getElementById("name").value,
            "location": (document.querySelector("#location .dropdown .select").textContent === 'Search location' || document.querySelector("#location .dropdown .select").textContent === '---.---') ? '' : document.querySelector("#location .dropdown .select").textContent,
            "area": (document.querySelector("#area .dropdown .select").textContent === 'Search area' || document.querySelector("#area .dropdown .select").textContent === '---.---') ? '' : document.querySelector("#area .dropdown .select").textContent,
            "vlan": document.querySelector("#vlan .dropdown .select").textContent==='Search VLAN'||'---.---'?'':document.querySelector("#vlan .dropdown .select").textContent
        }
        // console.log(conditions)
        probesContext.setConditions(conditions)
    }
    // const getProbes = setInterval(()=>{
    //     probesContext.probes
    // })
    return (
        <div className="probes">
            <div className="probes-action-buttonAdd">
                <Button className="addProbe-btn" onClick={handleOpenWindow}>
                    <FontAwesomeIcon icon={faSquarePlus} style={{ color: "#ffffff", }} />
                    <div className="btn-text">Add probes</div>
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
            {isOpen && <AddProbe handleCloseWindow={handleCloseWindow} ></AddProbe>}
            <ToastContainer ></ToastContainer>
            {probesContext.openDeleteScreen && <Confirm confirmContent={probesContext.deletedProbe} ></Confirm>}
        </div>
    );
}

export default Probes;