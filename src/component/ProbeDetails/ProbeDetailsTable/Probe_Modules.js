import { React, useState, useEffect } from 'react';
import { TableBody, TableCell, Tooltip, TableSortLabel, TablePagination } from '@mui/material';
import TableRow from '@mui/material/TableRow';
import Table from '@mui/material/Table';
import '../../../sass/ProbeDetails/ProbeDetailsTable/Probe_Modules.scss';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
    faTrashCan, faCircleRight, faXmarkCircle, faPenToSquare
} from '@fortawesome/free-regular-svg-icons'
import { faPlay, faArrowRotateLeft, faClockRotateLeft } from '@fortawesome/free-solid-svg-icons';
import Probe_Module_Header from './Probe_Module_Header';
import loading from '../../../assets/pic/ZKZg.gif';
import AddProbeModule from '../AddProbeModule';
const Probe_Modules = ({id}) => {
    const [isOpen,setOpenWindow] = useState(false)
    const [probe_modules, setProbeModules] = useState([])
    const [orderDirection, setOrderDirection] = useState('asc')
    const [valueToOrderBy, setValueToOrderBy] = useState('epw')
    const [page, setPage] = useState(0)
    const [rowsPerPage, setRowPerPage] = useState(7)
    const [displayPagination, setDisplayPagination] = useState(false)
    const [isEditedModule,setEditedModule] = useState(null)
    useEffect(() => {
        fetch("http://localhost:8081/api/v1/probe/modules?idProbe="+id+"&&name=&&status=")
            .then(response => response.json())
            .then(data => setProbeModules(data))
            .catch(err => console.log(err))
    }, [])

    const handleOpenWindow = (id) => {
        setOpenWindow(true)
        setEditedModule(id)
    }
    const handleCloseWindow = () => {
        setOpenWindow(false)
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
    const setLoading = (isLoading) => {
    }
    /*Phân trang*/ 
    const handleChangePage = (event, newPage) => {
        setPage(newPage);
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
    const actionWithModule = (id,action) =>{
        const options = {
            method : "POST",
            header :{
                "Content-Type": "application/json"
            }
        }
        fetch("http://localhost:8081/api/v1/probeModule/"+action+"?id="+id,options)
            .then(respone => respone.json())
            .then(data => {
                /*Xử lí data trả về*/ 
            })
    }
    /** Delete module */
    const deleteModule = (id) =>{
        const options ={
            method:"DELETE",
            header:{
                "Content-Type": "application/json"
            }
        }
        fetch("http://localhost:8081/api/v1/probeModule?id="+id,options)
            .then(respone => respone.json())
            .then(data => console.log(data))
            .catch(err => console.log(err))
    }
    return (
        <div className='Probe_Module'>
            <Table>
                <Probe_Module_Header
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
                                                        <button>
                                                            <FontAwesomeIcon icon={faPlay} style={{ color: "#00FF1A", }} />
                                                        </button>
                                                    </div>
                                                    <div className='action'>
                                                        <button>
                                                            <FontAwesomeIcon icon={faArrowRotateLeft} flip="horizontal" style={{ color: "#699BF7" }} />
                                                        </button>
                                                    </div>
                                                    <div className='action'>
                                                        <button >
                                                            <FontAwesomeIcon icon={faXmarkCircle} style={{ color: "#FF1C1C", }} />
                                                        </button>
                                                    </div>
                                                    <div className='action'>
                                                        <button >
                                                            <FontAwesomeIcon icon={faPenToSquare} style={{ color: "powderblue", }} onClick={()=>{
                                                                handleOpenWindow(module.id)
                                                            }} />
                                                        </button>
                                                    </div>
                                                    <div className='action'>
                                                        <button >
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
                                                    <img src={loading}></img>
                                                </div>
                                            </TableCell>
                                        </TableRow>
                                    )
                                })
                        ) : (
                            <TableRow style={{ border: "none" }}>
                                <TableCell colSpan={9} >Probe hiện tại chưa khởi tạo module nào</TableCell>
                            </TableRow>
                        )
                    }
                </TableBody>
                {
                    probe_modules.length == 0 ? false : true && <TablePagination
                        rowsPerPageOptions={[7, 8]}
                        component="div"
                        count={probe_modules.length}
                        rowsPerPage={rowsPerPage}
                        page={page}
                        onPageChange={handleChangePage}
                        onRowsPerPageChange={handleChangeRowsPerPage}
                    ></TablePagination>
                }
            </Table >
            {isOpen && <AddProbeModule id={isEditedModule} handleCloseWindow={handleCloseWindow}></AddProbeModule>}
        </div>
    )
}
export default Probe_Modules;