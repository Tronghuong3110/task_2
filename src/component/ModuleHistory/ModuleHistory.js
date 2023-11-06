import { useState, useEffect } from "react";
import 'bootstrap/dist/css/bootstrap.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCube, faMagnifyingGlass, faClockRotateLeft, faArrowRotateBack } from '@fortawesome/free-solid-svg-icons';
import { faPlusSquare, faTrashCan } from '@fortawesome/free-regular-svg-icons';
import '../../sass/ModuleHistory/ModuleHistory.scss'
import ModuleHistoryTable from "./ModuleHistoryTable/ModuleHistoryTable";
import { Checkbox ,Pagination} from "@mui/material";
import DropDownInput from "../action/DropDownInput";
import { IP } from "../Layout/constaints";

const ModuleHistory = () => {
    const [probes, setProbes] = useState([])
    const [modules, setProbeModules] = useState([])
    const [selectedProbe, setSelectedProbe] = useState("")
    const [selectedProbeModule, setSelectedProbeModule] = useState("")
    const [condition, setCondition] = useState({
        startDate: null,
        endDate: null,
        probeId: null,
        probeModuleId: null,
        content: null
    })
    useEffect(() => {
        fetch("http://" + IP + ":8081/api/v1/probes?name=&location=&area=&vlan=")
            .then(response => response.json())
            .then(data => {
                let arr = []
                data.map(ele => {
                    arr.push({
                        label: ele.name,
                        value: ele.id
                    })
                })
                console.log(arr)
                setProbes(arr)
            })
    }, [])
    useEffect(() => {
        if (selectedProbe != "" && selectedProbe != null) {
            console.log(selectedProbe)
            fetch("http://" + IP + ":8081/api/v1/probe/modules?idProbe=" + selectedProbe + "&&name=&&status=")
                .then(response => response.json())
                .then(data => {
                    let arr = []
                    data.map(ele => {
                        arr.push({
                            label: ele.moduleName,
                            value: ele.id
                        })
                    })
                    console.log(arr)
                    setProbeModules(arr)
                })
        }
        else setProbeModules([])
    }, [selectedProbe])
    function formatDate(date = new Date()) {
        const year = date.toLocaleString('default', {year: 'numeric'});
        const month = date.toLocaleString('default', {
          month: '2-digit',
        });
        const day = date.toLocaleString('default', {day: '2-digit'});
      
        return [year, month, day].join('-');
      }
    const getCondition = () => {
        let startDate = document.getElementById("startDate").value
        let endDate = document.getElementById("endDate").value
        let content = document.getElementById("content").value
        let tmp = {
            startDate: startDate,
            endDate: endDate,
            probeId: selectedProbe,
            probeModuleId: selectedProbeModule,
            content: content
        }
        setCondition(tmp)

    }
    return (
        <div className="modulesHistory">
            <div className='searchBar d-flex justify-content-between align-items-end'>
                <div className="searchDate">
                    <div className="conditionTitle">Start date</div>
                    <input type="date" placeholder="Choose date" id="startDate" defaultValue={formatDate(new Date())} ></input>
                </div>
                <div className="searchDate">
                    <div className="conditionTitle">End date</div>
                    <input type="date" placeholder="Choose date" id="endDate" defaultValue={formatDate(new Date())} ></input>
                </div>
                <div className="searchProbe">
                    <div className="conditionTitle">Probe name</div>
                    <DropDownInput defaultContent="Search probe" inputOptions={probes} handleSelect={setSelectedProbe} ></DropDownInput>
                </div>
                <div className="searchModule">
                    <div className="conditionTitle">Module name</div>
                    <DropDownInput defaultContent="Search module" inputOptions={modules} handleSelect={setSelectedProbeModule} ></DropDownInput>
                </div>
                <div className='searchTitle'>
                    <div className="conditionTitle">Content includes</div>
                    <input type='text' placeholder='Search by name...' id="content"></input>
                </div>
                <button className='searchButton d-flex'
                    onClick={getCondition}>
                    <div className='searchButton-icon'>
                        <FontAwesomeIcon icon={faMagnifyingGlass} rotation={90}></FontAwesomeIcon>
                    </div>
                    <div className='searchButton-text'>Search</div>
                </button>
            </div>
            <div className="actionBar d-flex align-items-center">
                <div className="checkAll">
                    <Checkbox
                        sx={{
                            color: 'white',
                            '&.Mui-checked': {
                                color: 'white',
                            },
                        }}
                    >

                    </Checkbox>
                </div>
                <div className="refreshButton">
                    <button>
                        <FontAwesomeIcon icon={faArrowRotateBack}></FontAwesomeIcon>
                    </button>
                </div>
                <div className="deleteButton">
                    <button>
                        <FontAwesomeIcon icon={faTrashCan}></FontAwesomeIcon>
                    </button>
                </div>
            </div>
            <ModuleHistoryTable condition = {condition}></ModuleHistoryTable>
            <div className='pagination d-flex justify-content-center'>
                <Pagination count={10}  color="secondary"  ></Pagination>
            </div>
        </div>
    )
}
export default ModuleHistory;