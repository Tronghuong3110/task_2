import { TableContainer, TableHead, TableRow, TableCell, TableBody, Table, Pagination, Box, Typography } from '@mui/material'
import React, { useState, useEffect } from 'react'
import '../../../sass/Nas/NasTable/NasTable.scss'
import {  faSquarePlus, faPenToSquare, faTrashCan } from '@fortawesome/free-regular-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import 'bootstrap/dist/css/bootstrap.css';
import Button from "@mui/material/Button"
// import AddDBServer from '../AddDBServer';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import '../../action/Confirm'
import Confirm from '../../action/Confirm';
import { IP } from '../../Layout/constaints';
import AddNas from '../AddNas';

function NASTable() {
    const [nasList, setNasList] = useState([
        // {
        //     "id": 1,
        //     "nasName": "NAS 01",
        //     "nasIp" : "192.168.100.122",
        //     "port":100,
        //     "description": "dat tai tru so"
        // },{
        //     "id": 2,
        //     "nasName": "NAS 02",
        //     "nasIp" : "192.168.100.112",
        //     "port":100,
        //     "description": "o dat tai tru so"
        // },
        // {
        //     "id": 3,
        //     "nasName": "NAS 03",
        //     "nasIp" : "192.168.100.132",
        //     "port":100,
        //     "description": "dat tai tru so"
        // }
    ])
    const [nasDisplay, setNasDisplay] = useState(nasList)
    const [findingKeyWord, setFindingKeyWord] = useState("")

    const [isOpenAddWindow, openCloseAddWindow] = useState(false);
    const [isEditNAS, setEditNAS] = useState(
        {
            "id": null,
            "nasName": null,
            "nasIp": null,
            "port": null,
            "description": null
        }
    )
    const [isOpenDeleteScreen, setOpenDeleteScreen] = useState(false)
    const [deletingNas, setDeletingNas] = useState({
        "id": null,
        "name": "",
        "message": "Are you sure to remove",
    })
    useEffect(() => {
        fetch(IP + "/api/v1/nases")
            .then(response => response.json())
            .then(data => {
                setNasList(data)
                setNasDisplay(data)
            }
            )
            .catch(err => console.log(err))
    }, [])

    useEffect(() => {
        var resultArray = nasList
        if (findingKeyWord !== "") {
            let keyword = findingKeyWord.toLowerCase()
            resultArray = (nasList.filter((item => (item.ip.includes(keyword) || item.nasName.toLowerCase().includes(keyword)))))
        }
        setNasDisplay(resultArray)

    }, [findingKeyWord])


    const getNasByCondition = () => {
        fetch(IP + "/api/v1/nases")
            .then(response => response.json())
            .then(data => {
                var resultArray = data
                if (findingKeyWord !== "") {
                    let keyword = findingKeyWord.toLowerCase()
                    resultArray = (resultArray.filter((item => (item.nasIp.includes(keyword) || item.nasName.toLowerCase().includes(keyword)))))
                }
                setNasDisplay(resultArray)
            })
            .catch(err=>console.log(err))
    }
    const handleOpenAddWindow = (id) => {
        openCloseAddWindow(true)
        setEditNAS({
            ...isEditNAS,
            "id": id
        })
    }
    const handleCloseAddWindow = () => {
        openCloseAddWindow(false)
    }

    const removeNas = (id, name) => {
        setOpenDeleteScreen(true)
        setDeletingNas({
            ...deletingNas,
            "id": id,
            "name": name
        })
    }
    const deleteNas = (id, userChoice) => {
        if (userChoice) {
            const options = {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json",
                }
            };
            fetch(IP + "/api/v1/nas?idNas=" + id, options)
                .then(response => response.json())
                .then(data => {
                    console.log(data)
                    notify(data.message, data.code)
                    getNasByCondition()
                }
                )
                .catch(err => console.log(err));
        }
        console.log(userChoice)
    }
    const notify = (message, status) => {
        if (status === 1) {
            toast.success(message, {
                position: "top-center",
                autoClose: 3000,
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
                autoClose: 3000,
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
                autoClose: 5000,
                hideProgressBar: false,
                closeOnClick: true,
                draggable: true,
                progress: undefined,
                theme: "colored",
            })
        }

    }

    return (
        <div className='NasTable'>
            <div className="NASManager-action-buttonAdd d-flex">
                <Button className="addProbe-btn" style={{ padding: "7px 15px" }}
                    onClick={() => handleOpenAddWindow(null)}
                >
                    <FontAwesomeIcon icon={faSquarePlus} style={{ color: "#ffffff" }} />
                    <div className="btn-text">Add NAS</div>
                </Button>
            </div>
            <div className="NASManager-action d-flex">
                <div className="NASManager-action-input">
                    <input type="text" placeholder='Search by name and ip address ...' id="name"
                        onChange={(e) => setFindingKeyWord(e.target.value)}
                        autoComplete='off'
                    />
                </div>
            </div>
            <TableContainer className='NasTableContainer' >
                <Table stickyHeader >
                    <TableHead sx={{ width: "100%" }} >
                        <TableRow>
                            <TableCell>#No</TableCell>
                            <TableCell>NAS Name</TableCell>
                            <TableCell>NAS IP</TableCell>
                            <TableCell>Port</TableCell>
                            <TableCell>Status</TableCell>
                            <TableCell>Path</TableCell>
                            <TableCell>Description</TableCell>
                            <TableCell>Action</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody sx={{ width: "100%" }}>
                        {nasDisplay.length !== 0 ? (nasDisplay.map((data, index) => {
                            return (
                                <TableRow key={data.id}>
                                    <TableCell>{data.id}</TableCell>
                                    <TableCell>{data.nasName}</TableCell>
                                    <TableCell>{data.ip}</TableCell>
                                    <TableCell>{data.port}</TableCell>
                                    <TableCell>Live</TableCell>
                                    <TableCell>{data.path}</TableCell>
                                    <TableCell>{data.description}</TableCell>
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
                                                    onClick={() => removeNas(data.id, data.nasName)}
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
            {isOpenAddWindow && <AddNas handleCloseWindow={handleCloseAddWindow} id={isEditNAS.id} rerender={getNasByCondition} />}
            <ToastContainer></ToastContainer>
            {
                isOpenDeleteScreen && <Confirm confirmContent={deletingNas} listDelete={[]} setOpenDeleteScreen={setOpenDeleteScreen} handleFunction={deleteNas} ></Confirm>
            }
        </div>
    )
}

export default NASTable