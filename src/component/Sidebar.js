import React from 'react';
import "../sass/Sidebar.scss";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import "@fontsource/montserrat";
import {
    faChartLine,
    faClockRotateLeft,
    faCube,
    faDisplay,
    faHouse
} from '@fortawesome/free-solid-svg-icons'
const Sidebar = () => {
    return (
        <div className="sidebar">
            <div className="sidebar-title d-flex justify-content-center">
                <div className='sidebar-title-text'>
                    Modules Manager
                </div>
            </div>
            <div className="sidebar-selections">
                <div className="selection d-flex" id="dashboard">
                    <div className="selection-symbol d-flex align-items-center">
                        <FontAwesomeIcon icon={faHouse} style={{ color: "#ffffff", fontSize: "1em" }} />
                    </div>
                    <div className="selection-text d-flex align-items-center">Dashbroad</div>
                </div>
                <div className="selection d-flex" id="modules">
                    <div className="selection-symbol d-flex align-items-center">
                        <FontAwesomeIcon icon={faCube} style={{ color: "#ffffff", fontSize: "1em" }} />
                    </div>
                    <div className="selection-text d-flex align-items-center">Modules</div>
                </div>
                <div className="selection d-flex" id="probes">
                    <div className="selection-symbol d-flex align-items-center">
                        <FontAwesomeIcon icon={faDisplay} style={{ color: "#ffffff", fontSize: "1em" }} />
                    </div>
                    <div className="selection-text d-flex align-items-center">Probes</div>
                </div>
                <div className="selection d-flex" id="history">
                    <div className="selection-symbol d-flex align-items-center">
                        <FontAwesomeIcon icon={faClockRotateLeft} style={{ color: "#ffffff", fontSize: "1em" }} />
                    </div>
                    <div className="selection-text d-flex align-items-center">History</div>
                </div>
                <div className="selection d-flex" id="statistic">
                    <div className="selection-symbol d-flex align-items-center">
                        <FontAwesomeIcon icon={faChartLine} style={{ color: "#ffffff", fontSize: "1em" }} />
                    </div>
                    <div className="selection-text d-flex align-items-center">Statistic</div>
                </div>
            </div>
        </div>
    )
}
export default Sidebar;