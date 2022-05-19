import React, {Component} from 'react';
import {SpreadSheets, Worksheet, Column} from '@grapecity/spread-sheets-react';
import './Style.css'
import dataService from '../dataService'

class OutlineCon extends Component {

    constructor(props) {
        super(props);
        this.state = {
            showRowOutline: true,
            showColumnOutline: true
        };
        this.hostStyle = {
            top: '90px',
            bottom: '35px'
        };
        this.rowOutlineInfo = [{index: 1, count: 4}, {index: 6, count: 4}];
        this.columnOutlineInfo = [{index: 0, count: 4}];
        this.autoGenerateColumns = false;
        this.data = dataService.getPersonAddressData();
    }

    changeProps(props, value) {
        let state = {};
        state[props] = value;
        this.setState(state);
    }

    render() {
        return (
            <div className="componentContainer" style={this.props.style}>
                <h3>윤곽선</h3>
                <div>
                    <p>아래 샘플은 윤곽선을 사용하는 방법을 보여줍니다.</p>
                </div>
                <div className="spreadContainer" style={this.hostStyle}>
                    <SpreadSheets>
                        <Worksheet
                            showRowOutline = {this.state.showRowOutline}
                            showColumnOutline = {this.state.showColumnOutline}
                            rowOutlineInfo = {this.rowOutlineInfo}
                            columnOutlineInfo = {this.columnOutlineInfo}
                            dataSource={this.data}
                            autoGenerateColumns={this.autoGenerateColumns}>
                            <Column width={150} dataField="Name"/>
                            <Column width={150} dataField="CountryRegionCode"/>
                            <Column width={100} dataField="City"/>
                            <Column width={200} dataField="AddressLine"/>
                            <Column width={100} dataField="PostalCode"/>
                        </Worksheet>
                    </SpreadSheets>
                </div>
                <div className="settingContainer">
                    <table>
                        <tbody>
                        <tr>
                            <td>
                                <label><input type="checkbox" checked={this.state.showRowOutline} onChange={(e) => {this.changeProps('showRowOutline', e.target.checked)}}/>행 개요 표시</label>
                            </td>
                            <td>
                                <label><input type="checkbox" checked={this.state.showColumnOutline} onChange={(e) => {this.changeProps('showColumnOutline', e.target.checked)}}/>열 개요 표시</label>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>

        )
    }
}

export default OutlineCon