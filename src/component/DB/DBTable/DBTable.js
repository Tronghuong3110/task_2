import { TableContainer, TableHead, TableRow, TableCell, TableBody, Table, Pagination, Box, Typography } from '@mui/material'
import React, { useState, useEffect } from 'react'
import 'bootstrap/dist/css/bootstrap.css';

import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import '../../action/Confirm'
import { IP } from '../../Layout/constaints';
import '../../../sass/DB/DBTable/DBTable.scss'

function DBTable() {
    const [dbList,setDbList] = useState([])
    const [dbDisplay,setDbDisplay] = useState(dbList)
    const [findingKeyWord, setFindingKeyWord] = useState("")
    const [dbType, setDbType] = useState("All")

    useEffect(()=>{
        fetch(IP+"/api/v1/info/volumes?name=&ip=&type=")
            .then(response => response.json())
            .then(data =>{
                let displayDB = data.filter(item => item.databaseName!="system")
                setDbList(displayDB)
                setDbDisplay(displayDB)
            })
            .catch(err => console.log(err))
    },[])

    useEffect(() => {
        var resultArray = dbList
        if (findingKeyWord !== "") {
            let keyword = findingKeyWord.toLowerCase()
            resultArray = (dbList.filter((item => (item.ipDb.includes(keyword) || item.databaseName.toLowerCase().includes(keyword)))))
        }
        if (dbType !== "All") {
            resultArray = resultArray.filter(item => item.type === dbType)
        }
        setDbDisplay(resultArray)

    }, [findingKeyWord,dbType])


    return (
        <div className='DBTable'>
            <div className="DB-action d-flex">
                <div className="DB-action-input">
                    <input type="text" placeholder='Search by name and ip address ...' id="name"
                        onChange={(e) => setFindingKeyWord(e.target.value)}
                        autoComplete='off'
                    />
                </div>
                <div className='DB-action-select'>
                    <select onChange={(e) => { setDbType(e.target.value) }}>
                        <option value="All">All</option>
                        <option value="SSD">SSD</option>
                        <option value="HDD">HDD</option>
                    </select>
                </div>
            </div>
            <TableContainer className='DBTableContainer' >
                <Table stickyHeader >
                    <TableHead sx={{ width: "100%" }} >
                        <TableRow>
                            <TableCell width="30%">Database Name</TableCell>
                            <TableCell width="15%">Database IP</TableCell>
                            <TableCell>Type</TableCell>
                            <TableCell>Volumn Total (GB)</TableCell>
                            <TableCell>Volumn Used (GB)</TableCell>
                            <TableCell>Volumn Free (GB)</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody sx={{ width: "100%" }}>
                        {dbDisplay.length != 0 ? (dbDisplay.map((data, index) => {
                            return (
                                <TableRow key={data.databaseName}>
                                    <TableCell>{data.databaseName}</TableCell>
                                    <TableCell>{data.ipDb}</TableCell>
                                    <TableCell>{data.type}</TableCell>
                                    <TableCell>{data.volumeTotal}</TableCell>
                                    <TableCell>{data.volumeUsed}</TableCell>
                                    <TableCell>{data.volumeFree}</TableCell>
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
            <Pagination className='dbServerPagnitation' count={10} color="secondary" />
            <ToastContainer></ToastContainer>
        </div>
    )
}

export default DBTable