import { useState, useEffect, useRef } from "react";
import 'bootstrap/dist/css/bootstrap.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faMagnifyingGlass} from '@fortawesome/free-solid-svg-icons';
import '../../sass/ModuleHistory/ModuleHistory.scss'
import ModuleHistoryTable from "./ModuleHistoryTable/ModuleHistoryTable";
import { Pagination } from "@mui/material";
import DropDownInput from "../action/DropDownInput";
import { IP } from "../Layout/constaints";
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { useParams } from "react-router-dom";
const ModuleHistory = () => {
    const {id} = useParams()
    const [probes, setProbes] = useState([])
    const [modules, setProbeModules] = useState([])
    const [selectedProbe, setSelectedProbe] = useState("")
    const [selectedProbeModule, setSelectedProbeModule] = useState("")
    const [selectedAck, setSelectedAck] = useState("")
    const [ack, setAck] = useState([
        {
            label: "Confirmed",
            value: "1"
        },
        {
            label: "Not confirmed",
            value: "0"
        }
    ])
    const [page, setPage] = useState(1)
    const [totalPage, setTotalPage] = useState(0)
    const [moduleHistories, setModuleHistories] = useState([])
    const [conditions, setConditions] = useState(()=>{
        if(id!=0){
            return {
                timeStart: null,
                timeEnd: null,
                idProbe: null,
                idProbeModule: id,
                content: null,
                ack: null
            }
        }
        else return{
            timeStart: null,
            timeEnd: null,
            idProbe: null,
            idProbeModule: null,
            content: null,
            ack: null
        }
    })
    const intervalRef = useRef(null);
    
    useEffect(() => {
        fetch(IP + "/api/v1/probes?name=&location=&area=&vlan=")
            .then(response => response.json())
            .then(data => {
                let arr = []
                data.map(ele => {
                    arr.push({
                        label: ele.name,
                        value: ele.id
                    })
                })
                setProbes(arr)
            })
    }, [])
    useEffect(() => {
        if (selectedProbe != "" && selectedProbe != null) {
            fetch(IP + "/api/v1/probe/modules?idProbe=" + selectedProbe + "&&name=&&status=")
                .then(response => response.json())
                .then(data => {
                    let arr = []
                    data.map(ele => {
                        arr.push({
                            label: ele.moduleName,
                            value: ele.id
                        })
                    })
                    setProbeModules(arr)
                })
        }
        else setProbeModules([])
    }, [selectedProbe])
    useEffect(() => {
        // console.log(conditions)
        getModuleHistory(conditions)
    }, [page,conditions])
    const getModuleHistory = (conditions) => {
        const fetchData = async () => {
            let api = IP + "/api/v1/moduleHistories?page=" + (page - 1);
            if (conditions != null) {
                for (let key in conditions) {
                    if (conditions[key] != "" && conditions[key] != null) api += "&" + key + "=" + conditions[key]
                }
                console.log(api)
            }
            fetch(api)
                .then(response => response.json())
                .then(data => {
                    console.log(data)
                    if (data.length === 0) {
                        setTotalPage(0)
                    }
                    else setTotalPage(data[0].totalPage)
                    setModuleHistories(data)
                })
                .catch(err => console.log(err))
        }
        // Clear previous interval
        if (intervalRef.current) {
            clearInterval(intervalRef.current);
        }

        // Fetch data immediately
        fetchData();

        // Fetch data every 5 seconds
        intervalRef.current = setInterval(fetchData, 5000);
    }
    useEffect(() => {
        return () => {
            if (intervalRef.current) {
                clearInterval(intervalRef.current);
            }
        };
    }, []);

    function formatDate(date = new Date()) {
        const year = date.toLocaleString('default', { year: 'numeric' });
        const month = date.toLocaleString('default', {
            month: '2-digit',
        });
        const day = date.toLocaleString('default', { day: '2-digit' });

        return [year, month, day].join('-');
    }
    const getByCondition = () => {
        let startDate = document.getElementById("startDate").value
        let endDate = document.getElementById("endDate").value
        let content = document.getElementById("content").value
        let tmp = {
            timeStart: startDate,
            timeEnd: endDate,
            idProbe: selectedProbe,
            idProbeModule: selectedProbeModule,
            content: content,
            ack: selectedAck
        }
        setConditions(tmp)
        setPage(1)
        getModuleHistory(tmp)
    }
    const handleChangePage = (event, newPage) => {
        setPage(newPage)
    }
    return (
        <div className="modulesHistory">
            <div className='searchBar d-flex justify-content-between align-items-end'>
                <div className="searchDate">
                    <div className="conditionTitle">Start date</div>
                    <input type="date" placeholder="Choose date" id="startDate" max={formatDate(new Date())} ></input>
                </div>
                <div className="searchDate">
                    <div className="conditionTitle">End date</div>
                    <input type="date" placeholder="Choose date" id="endDate"  max={formatDate(new Date())} ></input>
                </div>
                <div className="searchProbe">
                    <div className="conditionTitle">Probe name</div>
                    <DropDownInput defaultContent="Search probe" inputOptions={probes} handleSelect={setSelectedProbe} ></DropDownInput>
                </div>
                <div className="searchModule">
                    <div className="conditionTitle">Module name</div>
                    <DropDownInput defaultContent="Search module" inputOptions={modules} handleSelect={setSelectedProbeModule} ></DropDownInput>
                </div>
                <div className="searchAck">
                    <div className="conditionTitle">Confirm status</div>
                    <DropDownInput defaultContent="Search comfirm status" inputOptions={ack} handleSelect={setSelectedAck} ></DropDownInput>
                </div>
                <div className='searchTitle'>
                    <div className="conditionTitle">Content</div>
                    <input type='text' placeholder='Search by name...' id="content"></input>
                </div>
                <button className='searchButton d-flex'
                    onClick={getByCondition}>
                    <div className='searchButton-icon'>
                        <FontAwesomeIcon icon={faMagnifyingGlass} rotation={90}></FontAwesomeIcon>
                    </div>
                    <div className='searchButton-text'>Search</div>
                </button>
            </div>

            <ModuleHistoryTable moduleHistories={moduleHistories} getModuleHistory={getModuleHistory} conditions={conditions} ></ModuleHistoryTable>
            <div className='pagination d-flex justify-content-center'>
                <Pagination count={totalPage}
                    siblingCount={1}
                    color="secondary"
                    onChange={handleChangePage}
                    page={page}
                    showFirstButton showLastButton
                    boundaryCount={2}
                ></Pagination>
            </div>
            <ToastContainer></ToastContainer>
        </div>
    )
}
export default ModuleHistory;