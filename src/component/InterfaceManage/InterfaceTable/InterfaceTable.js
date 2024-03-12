import { TableContainer, TableHead, TableRow, TableCell, TableBody, Table, Box, Typography, Checkbox } from '@mui/material'
import React, { useState, useEffect } from 'react'
import '../../../sass/InterfaceManage/InterfaceTable/InterfaceTable.scss'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import 'bootstrap/dist/css/bootstrap.css';
import Button from "@mui/material/Button"
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import '../../action/Confirm'
import Confirm from '../../action/Confirm';
import { IP } from '../../Layout/constaints';
import InterfaceEditableInput from './InterfaceEditableInput';
import { faRotate } from '@fortawesome/free-solid-svg-icons';
import MonitorCheckbox from './MonitorCheckbox';

function InterfaceTable({id}) {
    const [interfaceList, setInterfaceList] = useState([])

    useEffect(() => {
        fetch(IP + "/api/v1/interface/list?idProbe=" + id)
                .then(res => res.json())
                .then(data => setInterfaceList(data))
                .catch(err => console.log(err))
        const interval = setInterval(() => {
            fetch(IP + "/api/v1/interface/list?idProbe=" + id)
                .then(res => res.json())
                .then(data => setInterfaceList(data))
                .catch(err => console.log(err))
        }, 15000)
        return () => {
            clearInterval(interval)
        }
    },[])
    return (
        <div className='InterfaceTable'>
            <div className="InterfaceManager-action-buttonAdd d-flex">
                <Button className="addProbe-btn" style={{ padding: "7px 15px" }}
                >
                    <FontAwesomeIcon icon={faRotate} style={{ color: "#ffffff" }} />
                    <div className="btn-text">Synchronize</div>
                </Button>
            </div>
            <TableContainer className='InterfaceTableContainer' sx={{ marginTop: '15px', overflowY: 'hidden' }} >
                <Table stickyHeader >
                    <TableHead sx={{ width: "100%" }} >
                        <TableRow>
                            <TableCell>Id</TableCell>
                            <TableCell>Interface Name</TableCell>
                            <TableCell>Interface Description</TableCell>
                            <TableCell>Monitor</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody sx={{ width: "100%" }}>
                        {interfaceList.length !== 0 ? (interfaceList.map((data, index) => {
                            return (
                                <TableRow key={data.id}>
                                    <TableCell>{data.id}</TableCell>
                                    <TableCell>{data.interfaceName}</TableCell>
                                    <TableCell>
                                        <InterfaceEditableInput idInterface={data.id} idProbe={id} initDescription={data.description} ></InterfaceEditableInput>
                                    </TableCell>
                                    <TableCell>
                                        <MonitorCheckbox idInterface={data.id} idProbe={id} initMonitor={data.monitor===1?true:false} />
                                    </TableCell>
                                </TableRow>
                            )
                        })) : (
                            <TableRow >
                                <TableCell colSpan={4} sx={{ border: "none" }}>
                                    <Box >
                                        <Typography>There is no item with your conditions</Typography>
                                    </Box>
                                </TableCell>
                            </TableRow>

                        )}
                    </TableBody>
                </Table>
            </TableContainer>
            <ToastContainer></ToastContainer>
            {/* {
                isOpenDeleteScreen && <Confirm confirmContent={deletingNas} listDelete={[]} setOpenDeleteScreen={setOpenDeleteScreen} handleFunction={deleteNas} ></Confirm>
            } */}
        </div>
    )
}

export default InterfaceTable