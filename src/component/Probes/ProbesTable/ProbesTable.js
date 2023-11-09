import { React, useState, useEffect, useContext } from 'react';
import { TableBody, TableCell, Tooltip, TablePagination } from '@mui/material';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Table from '@mui/material/Table';
import '../../../sass/Probes/ProbeTable/ProbesTable.scss';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
    faTrashCan, faCircleRight, faCirclePlay, faCircleStop
} from '@fortawesome/free-regular-svg-icons'
import { faCircle, faDownload } from '@fortawesome/free-solid-svg-icons';
import TableHeader from './TableHeader';
import { ProbesContext } from './ProbesContext';
import ConfigFileGenerator from '../../action/download';
import { Routes, Route, Link } from 'react-router-dom';
import Confirm from '../../action/Confirm';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { IP } from '../../Layout/constaints';
const ProbesTable = () => {
    const probesContext = useContext(ProbesContext)
    const [probes, setProbes] = useState(probesContext.probes)
    const [isOpenDeleteScreen, setOpenDeleteScreen] = useState(false)
    const [deletingProbe, setDeletingProbe] = useState({
        "id": null,
        "name": "",
        "message": "Are you sure to remove",
        "note": "You can recover it around 7 days or remove permanently in recycle bin",
    })
    const [orderDirection, setOrderDirection] = useState('asc')
    const [valueToOrderBy, setValueToOrderBy] = useState('running')
    const [page, setPage] = useState(0)
    const [rowsPerPage, setRowsPerPage] = useState(10)
    // const [displayPagination, setDisplayPagination] = useState(false)


    useEffect(() => {
        setProbes(probesContext.probes);
    }, [probesContext.probes]);
    const deletedProbe = (id,userChoice) => {
        if (userChoice && deletingProbe) {
            const options = {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            };
            fetch("http://" + IP + ":8081/api/v1/probe?id=" + id, options)
                .then(response => response.json())
                .then(data => {
                    const newArray = probes.filter(item => item.id !== id);
                    setProbes(newArray);
                    notify(data.message, data.code)
                })
                .catch(err => console.log(err))
        }
    }
    /*Sắp xếp theo status*/
    const handleRequestSort = (event, property) => {
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
    /*Phân trang*/
    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    }
    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value), 10)
        setPage(0)
    }
    const handleOnSwitch = (id, status) => {
        console.log("PUT")
        const requestOptions = {
            method: 'PUT',
            headers: {
                "Content-Type": "application/json; charset=utf8"
            },
            body: JSON.stringify({
                "id": id,
                "status": status == 'connected' ? "disconnected" : "connected"
            })
        };
        fetch("http://" + IP + ":8081/api/v1/probe", requestOptions)
            .then(respone => respone.json())
            .then(data => {
                console.log(data)
                if (data.code == "1") {
                    const updatedItems = probesContext.probes.map(item => {
                        if (item.id === id) {
                            return { ...item, status: status == 'connected' ? "disconnected" : "connected" };
                        }
                        return item;
                    })
                    probesContext.setProbes(updatedItems);
                    notify(data.message, data.code)
                }
                else {
                    notify(data.message, data.code)
                }
            })
            .catch(err => console.log(err))
    }
    const setIconConnect = (status) => {
        if (status == "connected") {
            return (
                <FontAwesomeIcon icon={faCircleStop} style={{ color: "#9A8383", }} />
            )
        }
        else {
            return (
                <FontAwesomeIcon icon={faCirclePlay} style={{ color: "#02F212", }} />
            )
        }
    }
    const removeProbe = (id, name) => {
        setOpenDeleteScreen(true)
        setDeletingProbe({
            ...deletingProbe,
            "id": id,
            "name": name
        })
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
    return (
        <div className='Probe_Table'>
            <Table>
                <TableHeader
                    orderDirection={orderDirection}
                    valueToOrderBy={valueToOrderBy}
                    handleRequestSort={handleRequestSort}
                ></TableHeader>
                <TableBody>
                    {
                        probesContext.probes.length != 0 ? (sortedProbes(probes, getComparator(orderDirection, valueToOrderBy))
                            .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                            .map((probe, index) => {
                                return (
                                    <TableRow key={probe.id} id={probe.id} >
                                        <TableCell className='id' >
                                            <div>{probe.id}</div>
                                        </TableCell>
                                        <TableCell className='probe_name' ><div>{probe.name}</div></TableCell>
                                        <TableCell className='ip_address' ><div>{probe.ipAddress}</div></TableCell>
                                        <TableCell className='location' >
                                            <Tooltip title={probe.location}><div>{probe.location}</div></Tooltip>
                                        </TableCell>
                                        <TableCell className='area' >
                                            <Tooltip title={probe.area}><div>{probe.area}</div></Tooltip>
                                        </TableCell>
                                        <TableCell className='vlan' ><div>{probe.vlan}</div></TableCell>
                                        <TableCell className='probeStatus'>
                                            <Tooltip title={probe.status}>
                                                <FontAwesomeIcon icon={faCircle}
                                                    color={(probe.status == 'connected') ? '#02F212' : (probe.status == "disconnected") ? '#9A8383' : '#ff0000'}
                                                ></FontAwesomeIcon>
                                            </Tooltip>
                                        </TableCell>
                                        <TableCell className='total_module' ><div>{probe.totalModule}</div></TableCell>
                                        <TableCell className='status' >
                                            <div className='status-container d-flex'>
                                                <div className='status-name running' key="numberRunningModule">{probe.numberRunningModule}</div>
                                                <div className='status-name pending' key="numberPendingModule">{probe.numberPendingModule}</div>
                                                <div className='status-name stopped' key="numberStoppedModule">{probe.numberStopedModule}</div>
                                                <div className='status-name failed' key="numberFailedModule">{probe.numberFailedModule}</div>
                                            </div>
                                        </TableCell>
                                        <TableCell className='last_online' >
                                            <div>{probe.connectAt}</div></TableCell>
                                        <TableCell className='description' >
                                            <Tooltip title={probe.description}><div>{probe.description}</div></Tooltip>
                                        </TableCell>
                                        <TableCell className='actions' >
                                            <div className='actions-container d-flex justify-content-around'>
                                                <div className='action'  >
                                                    <button onClick={() => { handleOnSwitch(probe.id, probe.status) }}>
                                                        {setIconConnect(probe.status)}
                                                    </button>
                                                </div>
                                                <div className='action'>
                                                    <button onClick={() => {
                                                        removeProbe(probe.id)
                                                    }}>
                                                        <FontAwesomeIcon icon={faTrashCan} style={{ color: "#cc3f3f", }} />
                                                    </button>
                                                </div>
                                                <div className='action'>
                                                    <ConfigFileGenerator id={probe.id}></ConfigFileGenerator>
                                                </div>
                                                <div className='action'>
                                                    <Link to={`/details/${probe.id}`} > <FontAwesomeIcon icon={faCircleRight} style={{ color: "#3f83f8", }} /></Link>
                                                </div>
                                            </div>
                                        </TableCell>
                                    </TableRow>
                                )
                            })) : (
                            <TableRow style={{ border: "none" }}>
                                <TableCell colSpan={15} style={{padding:"20px", fontWeight:"500", fontSize:"1.2em"}} >There is no probe in the list</TableCell>
                            </TableRow>
                        )

                    }
                </TableBody>
            </Table>
            {
                probes.length == 0 ? false : true && <TablePagination
                    rowsPerPageOptions={[7, 10]}
                    component="div"
                    count={probes.length}
                    rowsPerPage={rowsPerPage}
                    page={page}
                    onPageChange={handleChangePage}
                    onRowsPerPageChange={handleChangeRowsPerPage}
                ></TablePagination>
            }
            {isOpenDeleteScreen && <Confirm confirmContent={deletingProbe} listDelete={[]} setOpenDeleteScreen={setOpenDeleteScreen} handleFunction={deletedProbe} ></Confirm>}
        </div>
    )
}
export default ProbesTable;