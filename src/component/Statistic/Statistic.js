import { useState, useEffect } from "react";
import 'bootstrap/dist/css/bootstrap.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCube, faMagnifyingGlass, faClockRotateLeft, faArrowRotateBack } from '@fortawesome/free-solid-svg-icons';
import '../../sass/Statistic/Statistic.scss'
import { Checkbox, Pagination } from "@mui/material";
import DropDownInput from "../action/DropDownInput";
import { IP } from "../Layout/constaints";
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import StatisticTable from "./StastiticTable/StatisticTable";

const Statistic = () => {
    const [probes, setProbes] = useState([])
    const [selectedProbe, setSelectedProbe] = useState("")
    const [selectedModule, setSelectedModule] = useState("")

    const [page, setPage] = useState(1)
    const [totalPage, setTotalPage] = useState(0)
    const [modules, setModules] = useState([])
    const [conditions,setConditions] = useState(null)
    useEffect(() => {
        fetch("http://" + IP + "/api/v1/probes?name=&location=&area=&vlan=")
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
        fetch("http://" + IP + "/api/v1/modules")
            .then(response => response.json())
            .then(data => {
                let arr = []
                data.map(ele => {
                    arr.push({
                        label: ele.name,
                        value: ele.id
                    })
                })
                setModules(arr)
            })
    }, [])
    useEffect(() => {
        getModuleHistory(conditions)
    }, [page])
    const getModuleHistory = (conditions) =>{
        let api = "http://" + IP + "/api/v1/moduleHistories?page="+(page-1);
        if(conditions!=null){
            for(let key in conditions){
                if(conditions[key]!=""&&conditions[key]!=null) api+= "&"+key+"="+conditions[key]
            }
            console.log(api)
        }
        fetch(api)
        .then(response => response.json())
        .then(data => {
            if(data.length===0){
                setTotalPage(0)
            }
            else setTotalPage(data[0].totalPage==0?1:data[0].totalPage)
            // setModuleHistories(data)
        })
        .catch(err => console.log(err))
    }

    const getByCondition = () => {
        let tmp = {
            idProbe: selectedProbe,
            idProbeModule: selectedModule,
        }
        setConditions(tmp)
        getModuleHistory(tmp)
    }
    const handleChangePage = (event, newPage) => {
        setPage(newPage)
    }
    return (
        <div className="statistic">
            <div className='searchBar d-flex justify-content-between align-items-end'>
                <div className="searchProbe">
                    <div className="conditionTitle">Probe name</div>
                    <DropDownInput defaultContent="Search probe" inputOptions={probes} handleSelect={setSelectedProbe} ></DropDownInput>
                </div>
                <div className="searchModule">
                    <div className="conditionTitle">Sample module</div>
                    <DropDownInput defaultContent="Search module" inputOptions={modules} handleSelect={setSelectedModule} ></DropDownInput>
                </div>
                <div className='searchTitle'>
                    <div className="conditionTitle">Module name</div>
                    <input type='text' placeholder="Search by probe's modules name..." id="content"></input>
                </div>
                <div className='chooseTimeRange'>
                    <div className="conditionTitle">Time range</div>
                    <select>
                        <option>1 day</option>
                        <option>3 days</option>
                        <option>1 week </option>
                        <option>2 weeks</option>
                        <option>1 month</option>
                        <option>2 months</option>
                        <option>6 months</option>
                        <option>1 year</option>
                    </select>
                    
                </div>
                <button className='searchButton d-flex'
                    onClick={getByCondition}>
                    <div className='searchButton-icon'>
                        <FontAwesomeIcon icon={faMagnifyingGlass} rotation={90}></FontAwesomeIcon>
                    </div>
                    <div className='searchButton-text'>Search</div>
                </button>
            </div>
            <StatisticTable ></StatisticTable>
            {/* <div className='pagination d-flex justify-content-center'>
                <Pagination count={totalPage}
                    siblingCount={1}
                    color="secondary" 
                    onChange={handleChangePage} 
                    page={page} 
                    showFirstButton showLastButton
                    boundaryCount={2}
                ></Pagination>
            </div> */}
            <ToastContainer></ToastContainer>
        </div>
    )
}
export default Statistic;