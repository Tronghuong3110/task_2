import { React, useState, useEffect, useContext } from 'react';
import { TableBody, TableCell, Tooltip, TableSortLabel, TablePagination, TableContainer } from '@mui/material';
import TableRow from '@mui/material/TableRow';
import Table from '@mui/material/Table';
import '../../../sass/ProbeDetails/ProbeDetailsTable/Probe_Modules.scss';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
    faTrashCan, faCircleRight, faXmarkCircle, faPenToSquare, faSquarePlus
} from '@fortawesome/free-regular-svg-icons'
import { faPlay, faArrowRotateLeft, faClockRotateLeft, faCube, faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons';
import Probe_Module_Header from './Probe_Module_Header';
import loading from '../../../assets/pic/ZKZg.gif';
import AddProbeModule from '../AddProbeModule';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import Confirm from '../../action/Confirm';
import { IP } from '../../Layout/constaints';
const Probe_Modules = ({ id }) => {
    const [isOpen, setOpenWindow] = useState(false)
    const [probe_modules, setProbeModules] = useState([])
    const [isOpenDeleteScreen, setOpenDeleteScreen] = useState(false)
    const [deletingProbeModule, setDeletingProbeModule] = useState({
        "id": null,
        "name": "",
        "message": "Are you sure to remove",
        "note": "It will be removed permanently",
    })
    const [orderDirection, setOrderDirection] = useState('asc')
    const [valueToOrderBy, setValueToOrderBy] = useState('epw')
    const [page, setPage] = useState(0)
    const [rowsPerPage, setRowPerPage] = useState(10)
    const [displayPagination, setDisplayPagination] = useState(false)
    const [isEditedModule, setEditedModule] = useState(null)
    const [fullModules, setFullModules] = useState([]);
    const [selectedProbeModules, setSelectedProbeModules] = useState([])
    const [conditions, setConditions] = useState({
        "name": "",
        "status": "All"
    })
    const [checkAllPages, setCheckAllPages] = useState([])
    const [existRunningOrPending,setExistRunningOrPending] = useState([])
    useEffect(() => {
        getProbeModules()
    }, [])
    useEffect(() => {
        let name = conditions.name;
        let status = conditions.status;
        let result = fullModules.filter(modules => modules.moduleName.includes(name) && (modules.status == (status == "All" ? modules.status : status)))
        setProbeModules(result)
    }, [conditions])
    const getProbeModules = () => {
        console.log("Hi")
        fetch("http://" + IP + ":8081/api/v1/probe/modules?idProbe=" + id + "&&name=&&status=")
            .then(response => response.json())
            .then(data => {
                setFullModules(data)
                setProbeModules(data)
                setExistRunningOrPending(data.filter(item => item.status == "Running"|| item.status =="Pending"))
            })
            .catch(err => console.log(err))
    }
    const getProbeModulesByConditions = () => {
        fetch("http://" + IP + ":8081/api/v1/probe/modules?idProbe=" + id + "&&name=&&status=")
            .then(response => response.json())
            .then(data => {
                let name = document.getElementById("keyWordInput").value;
                let status = document.getElementById("statusInput").value;
                console.log(name, status)
                // setConditions({
                //     "name": name,
                //     "status":status
                // })
                let result = data.filter(modules => modules.moduleName.includes(name) && (modules.status == (status == "All" ? modules.status : status)))
                let check = result.filter(item => item.status == "Running"|| item.status =="Pending")
                setExistRunningOrPending(check)
                setProbeModules(result)
            })
            .catch(err => console.log(err))
    }
    const handleOpenWindow = (id) => {
        setOpenWindow(true)
        setEditedModule(id)
    }
    const handleCloseWindow = () => {
        setOpenWindow(false)
        getProbeModules()
    }
    /*Sắp xếp theo status*/
    const setStatusColor = (status) => {
        switch (status) {
            case "Running":
                return "#00FF1A";
            case "Pending":
                return "#FFD233";
            case "Stopped":
                return "#FF1CE8";
            case "Failed":
                return "#FF1C1C";
            default:
                return "white"
        }
    }
    /*Phân trang*/
    const handleChangePage = (event, newPage) => {
        setPage(newPage);
        console.log(checkAllPages.find(item => parseInt(item) == parseInt(newPage)))
        if (checkAllPages.find(item => item == newPage) != undefined) document.getElementById("main-tick").checked = true;
        else document.getElementById("main-tick").checked = false;
        console.log(checkAllPages)
    }
    const handleChangeRowsPerPage = (event) => {
        setRowPerPage(parseInt(event.target.value), 10)
        setPage(0)
    }
    /* Sắp xếp theo điều kiện EPW */
    const handleRequestSort = (event, property) => {
        console.log(property)
        const isAscending = (valueToOrderBy === property && orderDirection === 'asc')
        setValueToOrderBy(property)
        setOrderDirection(isAscending ? 'desc' : 'asc')
    }
    function descendingComparator(a, b, orderBy) {
        if (b[orderBy] < a[orderBy]) {
            return -1;
        }
        if (b[orderBy] > a[orderBy]) {
            return 1;
        }
        return 0;
    }
    function getComparator(order, orderBy) {
        return order === "desc"
            ? (a, b) => descendingComparator(a, b, orderBy)
            : (a, b) => -descendingComparator(a, b, orderBy)
    }
    const sortedProbes = (arr, comparator) => {
        const stablilizeRowArray = arr.map((el, index) => [el, index])
        stablilizeRowArray.sort((a, b) => {
            const order = comparator(a[0], b[0])
            if (order !== 0) return order
            return a[1] - b[1]
        })
        return stablilizeRowArray.map((el) => el[0])
    }
    /** Run or Restart or Stop module */
    const actionWithModule = (id, action) => {
        let nodes = document.querySelectorAll(".actionBtn")
        nodes.forEach(node => {
            node.setAttribute("disabled", true)
            setTimeout(() => {
                node.removeAttribute("disabled");
            }, 1000);
        })
        let check = 0;
        fetch("http://" + IP + ":8081/api/v1/probeModule/" + action)
            .then(response => response.text())
            .then(data => {
                notify("Received request succesfully", 1)
            })
            .then(() => {
                let stringParam;
                if (Array.isArray(id)) stringParam = id.join(" ")
                else stringParam = id
                const options = {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    }
                }
                fetch("http://" + IP + ":8081/api/v1/probeModule/" + action + "?idProbeModule=" + stringParam, options)
                    .then(response => response.text())
                    .then(data => {
                        setTimeout(() => { check = data }, 2000)
                    })
                    .catch(err => console.log(err))
            })
        setInterval(() => {
            if (check == 1) {
                return;
            }
            else {
                getProbeModulesByConditions()
            }
        }, 2000);
    }
    /** Delete 1 module */
    const displayDeleteModule = (id, name) => {
        setOpenDeleteScreen(true)
        setDeletingProbeModule({
            ...deletingProbeModule,
            "id": id,
            "name": "module " + name
        })
    }
    const displayDeleteMultiModule = () => {
        setOpenDeleteScreen(true)

    }
    const deleteModule = (id, userChoice) => {
        if (userChoice) {
            let stringParam =""
            if (Array.isArray(id)) {
                console.log(id, existRunningOrPending)
                console.log(existRunningOrPending.some(item => id.includes(item.id)))
                if(existRunningOrPending.some(item => id.includes(item.id))){
                    notify("There are some modules running or pending, please stop before delete them",2)
                    return;
                }
                stringParam = id.join(" ")
            }
            else stringParam = id
            const options = {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json",
                }
            };
            fetch("http://" + IP + ":8081/api/v1/probeModule?id=" + stringParam, options)
                .then(response => response.json())
                .then(data => {
                    console.log(data)
                    notify(data.message, data.code)
                    getProbeModules()
                }
                )
                .catch(err => console.log(err));
        }
    }
    /** Add to checked list */
    const addOrRemoveToCheckedList = (id) => {
        let checkedValue = document.getElementById(id).checked
        if (checkedValue == true) {
            setSelectedProbeModules([...selectedProbeModules, id])
        }
        else {
            console.log(selectedProbeModules.filter(item => item != id))
            setSelectedProbeModules(selectedProbeModules.filter(item => item != id))
        }
    }
    const selectOrRemoveALL = (value) => {
        let arrNum = [];
        let listCheckBox = document.querySelectorAll(".checkbox .checkbox-input")
        listCheckBox.forEach(node => {
            if (!selectedProbeModules.find(item => item == node.id)) arrNum.push(parseInt(node.id))
        });
        if (value == true) {
            console.log(arrNum)
            setSelectedProbeModules(selectedProbeModules.concat(arrNum));
            setCheckAllPages([...checkAllPages, page])
        }
        else {
            setSelectedProbeModules(selectedProbeModules.filter(item => arrNum.includes(item)))
            setCheckAllPages(checkAllPages.filter(item => item != page))
        }
    }
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
                autoClose: 4000,
                hideProgressBar: false,
                closeOnClick: true,
                draggable: true,
                progress: undefined,
                theme: "colored",
            })
        }

    }
    const getKeyWord = (e) => {
        setConditions({
            ...conditions,
            name: e.target.value
        })
    }
    const getStatus = (e) => {
        setConditions({
            ...conditions,
            status: e.target.value
        })
    }
    const isSelected = (id) => {
        if (selectedProbeModules.find(num => num == id) == undefined) return false;
        else return true;
    }

    return (
        <div className='Probe_Module'>
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
                                <input id='keyWordInput' type="text" placeholder="Search "
                                    onChange={getKeyWord}
                                ></input>
                                <FontAwesomeIcon icon={faMagnifyingGlass}></FontAwesomeIcon>
                            </div>
                        </div>
                        <div className="searchBar-searchStatus">
                            <div className="searchBar-searchStatus-select">
                                <select id="statusInput" onChange={getStatus}>
                                    <option value="All" >All</option>
                                    <option value="Running">Running</option>
                                    <option value="Pending">Pending</option>
                                    <option value="Stopped">Stopped</option>
                                    <option value="Failed">Failed</option>
                                </select>
                            </div>
                        </div>
                        <button className="addBtn d-flex align-items-center" onClick={() => handleOpenWindow(null)}>
                            <div className="addBtn-icon"><FontAwesomeIcon icon={faSquarePlus}></FontAwesomeIcon></div>
                            <div className="addBtn-text ">New module</div>
                        </button>
                    </div>
                </div>
            </div>
            <div className='actions-outside-container d-flex'>
                <div className='action'>
                    <Tooltip title="Run all selected modules">
                        <button
                            onClick={() => {
                                actionWithModule(selectedProbeModules, "run")
                            }}
                        >
                            <FontAwesomeIcon icon={faPlay} style={{ color: "#00FF1A", }} />
                        </button>
                    </Tooltip>
                </div>
                <div className='action'>
                    <Tooltip title="Restart all selected modules">
                        <button
                            // disabled={module.loading}
                            onClick={() => {
                                actionWithModule(selectedProbeModules, "restart")
                            }}
                        >
                            <FontAwesomeIcon icon={faArrowRotateLeft} flip="horizontal" style={{ color: "#699BF7" }} />
                        </button>
                    </Tooltip>
                </div>
                <div className='action'>
                    <Tooltip title="Stop all selected modules">
                        <button
                            // disabled={module.loading}
                            onClick={() => {
                                actionWithModule(selectedProbeModules, "stop")
                            }}
                        >
                            <FontAwesomeIcon icon={faXmarkCircle} style={{ color: "#FF1C1C", }} />
                        </button>
                    </Tooltip>
                </div>
                <div className='action'>
                    <Tooltip title="Delete all selected modules">
                        <button
                            // disabled={module.loading}
                            onClick={() => {
                                // deleteMultiModules()
                                displayDeleteMultiModule()
                            }}
                        >
                            <FontAwesomeIcon icon={faTrashCan} style={{ color: "#FFD233", }} />
                        </button>
                    </Tooltip>
                </div>
            </div>
            <TableContainer className='table-container'>
                <Table>
                    <Probe_Module_Header
                        selectOrRemoveALL={selectOrRemoveALL}
                        orderDirection={orderDirection}
                        valueToOrderBy={valueToOrderBy}
                        handleRequestSort={handleRequestSort}
                    ></Probe_Module_Header>
                    <TableBody>
                        {
                            probe_modules.length != 0 ? (
                                sortedProbes(probe_modules, getComparator(orderDirection, valueToOrderBy))
                                    .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                                    .map((module, index) => {
                                        return (

                                            <TableRow key={module.id} >
                                                <TableCell className='checkbox'>
                                                    <input defaultChecked={false} disabled={module.loading} checked={isSelected(module.id)} className='checkbox-input' id={module.id} type='checkbox' onChange={() => {
                                                        addOrRemoveToCheckedList(module.id)
                                                    }}></input>
                                                </TableCell>
                                                <TableCell className='id' >
                                                    <div>{module.id}</div>
                                                </TableCell>
                                                <TableCell className='module_name' ><div>{module.moduleName}</div></TableCell>
                                                <TableCell className='caption' ><div>{module.caption}</div></TableCell>
                                                <TableCell className='argument' ><div>{module.arg}</div></TableCell>
                                                <TableCell className='errorPerWeek' ><div key="errorPerWeek">{module.errorPerWeek}</div></TableCell>
                                                <TableCell className='status' ><div style={{ color: setStatusColor(module.status) }}>{module.status}</div></TableCell>
                                                <TableCell className='note' ><div>{module.note}</div></TableCell>
                                                <TableCell className='actions' >
                                                    <div className='actions-container d-flex justify-content-between'>
                                                        <div className='action'>
                                                            <button className='actionBtn runBtn'
                                                                disabled={module.loading}
                                                                onClick={() => {
                                                                    actionWithModule(module.id, "run")
                                                                }}
                                                            >
                                                                <FontAwesomeIcon icon={faPlay} style={{ color: "#00FF1A", }} />
                                                            </button>
                                                        </div>
                                                        <div className='action'>
                                                            <button className='actionBtn reStartBtn'
                                                                disabled={module.loading}
                                                                onClick={() => {
                                                                    actionWithModule(module.id, "restart")
                                                                }}
                                                            >
                                                                <FontAwesomeIcon icon={faArrowRotateLeft} flip="horizontal" style={{ color: "#699BF7" }} />
                                                            </button>
                                                        </div>
                                                        <div className='action'>
                                                            <button className='actionBtn stopBtn'
                                                                disabled={module.loading || (module.status == "Stopped" || module.status == "Failed") ? true : false}
                                                                onClick={() => {
                                                                    actionWithModule(module.id, "stop")
                                                                }}
                                                            >
                                                                <FontAwesomeIcon icon={faXmarkCircle} style={{ color: "#FF1C1C", }} />
                                                            </button>
                                                        </div>
                                                        <div className='action'>
                                                            <button
                                                                disabled={module.loading || (module.status == "Running" || module.status == "Pending") ? true : false}
                                                            >
                                                                <FontAwesomeIcon icon={faPenToSquare} style={{ color: "powderblue", }} onClick={() => {
                                                                    handleOpenWindow(module.id)
                                                                }} />
                                                            </button>
                                                        </div>
                                                        <div className='action'>
                                                            <button
                                                                disabled={module.loading || (module.status == "Running" || module.status == "Pending") ? true : false}
                                                                onClick={() => {
                                                                    displayDeleteModule(module.id, module.moduleName)
                                                                }}
                                                            >
                                                                <FontAwesomeIcon icon={faTrashCan} style={{ color: "#FFD233", }} />
                                                            </button>
                                                        </div>
                                                        <div className='action'>
                                                            <button >
                                                                <FontAwesomeIcon icon={faClockRotateLeft} style={{ color: "#FF1CE8", }} />
                                                            </button>
                                                        </div>
                                                    </div>
                                                </TableCell>
                                                <TableCell className='processStatus' >
                                                    <div>
                                                        {(module.loading == 1 ? true : false) && <img src={loading} ></img>}
                                                    </div>
                                                </TableCell>
                                            </TableRow>
                                        )
                                    })
                            ) : (
                                <TableRow style={{ border: "none" }}>
                                    <TableCell colSpan={9} >Hiện không tìm thấy module nào</TableCell>
                                </TableRow>
                            )
                        }
                    </TableBody>
                </Table >
                {
                    probe_modules.length == 0 ? false : true && <TablePagination
                        rowsPerPageOptions={[10, 15, 20]}
                        component="div"
                        count={probe_modules.length}
                        rowsPerPage={rowsPerPage}
                        page={page}
                        onPageChange={handleChangePage}
                        onRowsPerPageChange={handleChangeRowsPerPage}
                    ></TablePagination>
                }
            </TableContainer>
            {isOpen && <AddProbeModule id={isEditedModule} handleCloseWindow={handleCloseWindow} idProbe={id} ></AddProbeModule>}
            <ToastContainer></ToastContainer>
            {isOpenDeleteScreen && <Confirm confirmContent={deletingProbeModule} listDelete={selectedProbeModules} setOpenDeleteScreen={setOpenDeleteScreen} handleFunction={deleteModule} ></Confirm>}
        </div>
    )
}
export default Probe_Modules;