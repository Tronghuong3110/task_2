import { useState, useEffect } from 'react';
import 'bootstrap/dist/css/bootstrap.css';
import { TableRow, Table, TableHead, TableCell, TableBody,TableContainer,Tooltip } from "@mui/material";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import '../../../sass//StatisticTable.scss'
import { IP } from '../../Layout/constaints'
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import StatisticTableHeader from './StatisticTableHeader';
const StatisticTable = (props) => {
    const [probe_modules, setProbeModules] = useState([])
    const [orderDirection, setOrderDirection] = useState('asc')
    const [valueToOrderBy, setValueToOrderBy] = useState('epw')
    const [page, setPage] = useState(0)
    const [rowsPerPage, setRowPerPage] = useState(10)

    /* Sắp xếp theo điều kiện EPW */
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
        <div className='Statistic_Table'>
            <TableContainer className='table-container'>
                <Table>
                    <StatisticTableHeader
                        orderDirection={orderDirection}
                        valueToOrderBy={valueToOrderBy}
                        handleRequestSort={handleRequestSort}
                    ></StatisticTableHeader>
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
                                                <TableCell className='argument' ><div>
                                                    <Tooltip title={module.arg}>
                                                        {module.arg}
                                                    </Tooltip>
                                                </div></TableCell>
                                                <TableCell className='errorPerWeek' ><div key="errorPerWeek">{module.errorPerWeek}</div></TableCell>
                                                <TableCell className='note' ><div>{module.note}</div></TableCell>
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
        </div>
    )
}
export default StatisticTable