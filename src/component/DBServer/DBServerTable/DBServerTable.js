import { TableContainer, TableHead, TableRow, TableCell, TableBody, Table, Box, Typography } from '@mui/material'
import React, { useState, useEffect } from 'react'
import '../../../sass/DBServer/DBServerTable/DBServerTable.scss'
import {  faSquarePlus, faPenToSquare, faTrashCan } from '@fortawesome/free-regular-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import 'bootstrap/dist/css/bootstrap.css';
import Button from "@mui/material/Button"
import AddDBServer from '../AddDBServer';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import '../../action/Confirm'
import Confirm from '../../action/Confirm';
import { IP } from '../../Layout/constaints';
function DBServerTable() {

    const [isOpenAddWindow, openCloseAddWindow] = useState(false);
    const [dbServer, setDBServer] = useState([])
    const [findingKeyWord, setFindingKeyWord] = useState("")
    const [serverType, setServerType] = useState("All")
    const [dbServerDisplay, setDBServerDisplay] = useState(dbServer)
    const [isEditDBServer, setEditDBServer] = useState(
        {
            "id": null,
            "ipServer": "",
            "serverName": "",
            "type": "SSD",
            "description": "",
            "dbAccount": "",
            "dbPass": "",
            "sshAccount": "",
            "sshPass": "",
            "nasId": null,
            "nasName": ""
        }
    )
    const [deletingServer, setDeletingServer] = useState({
        "id": null,
        "name": "",
        "message": "Are you sure to remove",
        "note": "This module will be removed permanently",
    })
    const [isOpenDeleteScreen, setOpenDeleteScreen] = useState(false)
    useEffect(() => {
        fetch(IP + "/api/v1/database/servers?key=")
            .then(response => response.json())
            .then(data => {
                setDBServer(data)
                setDBServerDisplay(data)
            })
            .catch(err => console.log(err))

    }, [])
    useEffect(() => {
        var resultArray = dbServer
        if (findingKeyWord != "") {
            let keyword = findingKeyWord.toLowerCase()
            resultArray = (dbServer.filter((item => (item.ipServer.includes(keyword) || item.serverName.toLowerCase().includes(keyword)))))
        }
        if (serverType !== "All") {
            resultArray = resultArray.filter(item => item.type === serverType)
        }
        setDBServerDisplay(resultArray)

    }, [findingKeyWord, serverType])
    const getDBServerByCondition = () => {
        fetch(IP + "/api/v1/database/servers?key=")
            .then(response => response.json())
            .then(data => {
                let result = data
                if (findingKeyWord != "") result = result.filter((item => (item.ipServer.includes(findingKeyWord) || item.serverName.includes(findingKeyWord))))
                if (serverType !== "All") {
                    result = result.filter(item => item.type === serverType)
                }
                setDBServerDisplay(result)

            })
            .catch(err => console.log(err))
    }
    // Hàm đóng mở cửa sổ thêm mới DBServer
    const handleOpenAddWindow = (id) => {
        openCloseAddWindow(true)
        setEditDBServer({
            ...isEditDBServer,
            "id": id
        })
    }
    const handleCloseAddWindow = () => {
        openCloseAddWindow(false)
    }

    const removeServer = (id, name) => {
        setOpenDeleteScreen(true)
        setDeletingServer({
            ...deletingServer,
            "id": id,
            "name": name
        })
    }
    const deleteServer = (id, userChoice) => {
        if (userChoice) {
            const options = {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json",
                }
            };
            fetch(IP + "/api/v1/database/server?id=" + id, options)
                .then(response => response.json())
                .then(data => {
                    console.log(data)
                    notify(data.message, data.code)
                    getDBServerByCondition()
                }
                )
                .catch(err => console.log(err));
        }
    }
    //Hàm hiển thị thông báo sau khi thêm
    const notify = (message, status) => {
        if (status === 1) {
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
        else if (status === 0) {
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
        <div className='DBServerTable'>
            <div className="DBServer-action-buttonAdd d-flex">
                <Button className="addProbe-btn" style={{ padding: "7px 15px" }}
                    onClick={() => handleOpenAddWindow(null)}
                >
                    <FontAwesomeIcon icon={faSquarePlus} style={{ color: "#ffffff" }} />
                    <div className="btn-text">Add server</div>
                </Button>
            </div>
            <div className="DBServer-action d-flex">
                <div className="DBServer-action-input">
                    <input type="text" placeholder='Search by name...' id="name"
                        onChange={(e) => setFindingKeyWord(e.target.value)}
                        autoComplete='off'
                    />
                </div>
                <div className='DBServer-action-select'>
                    <select onChange={(e) => { setServerType(e.target.value) }}>
                        <option value="All">All</option>
                        <option value="SSD">SSD</option>
                        <option value="HDD">HDD</option>
                    </select>
                </div>
            </div>
            <TableContainer className='DBServerTableContainer' >
                <Table stickyHeader >
                    <TableHead sx={{ width: "100%" }} >
                        <TableRow>
                            <TableCell>Id</TableCell>
                            <TableCell>Server IP</TableCell>
                            <TableCell>Server Name</TableCell>
                            <TableCell>Type</TableCell>
                            <TableCell>Backup NAS</TableCell>
                            <TableCell>Description</TableCell>
                            <TableCell>DB Account</TableCell>
                            <TableCell>SSH Account</TableCell>
                            <TableCell>Action</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody sx={{ width: "100%" }}>
                        {dbServerDisplay.length != 0 ? (dbServerDisplay.map((data, index) => {
                            return (
                                <TableRow key={data.id}>
                                    <TableCell>{data.id}</TableCell>
                                    <TableCell>{data.ipServer}</TableCell>
                                    <TableCell>{data.serverName}</TableCell>
                                    <TableCell>{data.type}</TableCell>
                                    <TableCell>{data.nasName}</TableCell>
                                    <TableCell>{data.description}</TableCell>
                                    <TableCell>{data.dbAccount}</TableCell>
                                    <TableCell>{data.sshAccount}</TableCell>
                                    <TableCell>
                                        <div className='d-flex justify-content-center'>
                                            <div className='action'>
                                                <button
                                                    onClick={() => handleOpenAddWindow(data.id)}
                                                >
                                                    <FontAwesomeIcon icon={faPenToSquare} style={{ color: "#699BF7" }} />
                                                </button>
                                            </div>
                                            <div className='action'>
                                                <button
                                                    onClick={() => removeServer(data.id, data.serverName)}
                                                >
                                                    <FontAwesomeIcon icon={faTrashCan} style={{ color: "#FFD233" }} />
                                                </button>
                                            </div>
                                        </div>
                                    </TableCell>
                                </TableRow>
                            )
                        })) : (
                            <TableRow >
                                <TableCell colSpan={9} sx={{ border: "none" }}>
                                    <Box >
                                        <Typography>There is no item with your conditions</Typography>
                                    </Box>
                                </TableCell>
                            </TableRow>

                        )}
                    </TableBody>
                </Table>
            </TableContainer>
            {/* <Pagination className='dbServerPagnitation' count={10} color="secondary" /> */}
            {isOpenAddWindow && <AddDBServer handleCloseWindow={handleCloseAddWindow} id={isEditDBServer.id} rerender={getDBServerByCondition} />}
            <ToastContainer></ToastContainer>
            {
                isOpenDeleteScreen && <Confirm confirmContent={deletingServer} listDelete={[]} setOpenDeleteScreen={setOpenDeleteScreen} handleFunction={deleteServer} ></Confirm>
            }
        </div>
    )
}

export default DBServerTable