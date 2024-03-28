import React from 'react'
import DBTable from './DBTable/DBTable'
import '../../sass/DB/DB.scss'
import { useParams } from 'react-router-dom'
function DB() {
    const {ipServer} = useParams()

    return (
        <div className='DB'>
            <DBTable ipServer={ipServer} />
        </div>
    )
}

export default DB