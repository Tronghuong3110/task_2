import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Box, Typography } from '@mui/material'
import React from 'react'
import '../../sass/BackupRestoreHistory/BackupRestoreHistory.scss'

const BackupRestoreHistory = () => {
    const dbServerDisplay = []
    return (
        <div className='BackupRestoreHistory'>
            <div className='BackupRestoreHistoryTable'>
                <div className='searchBar d-flex justify-content-between align-items-end'>
                    <div className='searchTitle'>
                        <div className="conditionTitle">Source</div>
                        <input type='text' placeholder="Search by probe name..." id="content"></input>
                    </div>
                    <div className='searchTitle'>
                        <div className="conditionTitle">Destination</div>
                        <input type='text' placeholder="Search by probe name..." id="content"></input>
                    </div>
                    <div className='chooseTimeRange'>
                        <div className="conditionTitle">Action type</div>
                        <select id='monitorStatus'>
                            <option value="">All</option>
                            <option value="Monitor">Backup</option>
                            <option value="No monitor">Restore</option>
                        </select>
                    </div>
                    <div className='chooseTimeRange'>
                        <div className="conditionTitle">Status</div>
                        <select id='monitorStatus'>
                            <option value="">All</option>
                            <option value="Monitor">Finished</option>
                            <option value="No monitor">Error</option>
                        </select>
                    </div>
                    <div className="searchProbe searchDate">
                        <div className="conditionTitle">Start date</div>
                        <input type='date'></input>
                    </div>
                    <div className="searchProbe searchDate">
                        <div className="conditionTitle">Completed date</div>
                        <input type='date'></input>
                    </div>
                </div>
                <TableContainer className='BackupRestoreHistoryTableContainer' >
                    <Table stickyHeader >
                        <TableHead sx={{ width: "100%" }} >
                            <TableRow>
                                <TableCell width="15%">Source</TableCell>
                                <TableCell width="15%">Destination</TableCell>
                                <TableCell width="10%">Action type</TableCell>
                                <TableCell width="10%">Status</TableCell>
                                <TableCell width="10%">Start time</TableCell>
                                <TableCell width="10%">Complete time</TableCell>
                                <TableCell width="30%">Log details</TableCell>
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
                                        <TableCell>{data.serverName}</TableCell>
                                        <TableCell>{data.type}</TableCell>
                                        <TableCell>
                                            {data.ipServer}
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
            </div>
        </div>
    )
}

export default BackupRestoreHistory